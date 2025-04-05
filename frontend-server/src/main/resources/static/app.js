let socket = null;
let reconnectAttempts = 0;
const maxReconnectAttempts = 5;
const reconnectDelay = 2000; // 2 seconds
let currentBreakpoints = [];
let selectedBreakpoint = null;

function connectWebSocket() {
    const wsUrl = `ws://${window.location.host}/ws/breakpoints`;

    if (socket) {
        socket.close();
    }

    socket = new WebSocket(wsUrl);

    socket.onopen = function() {
        console.log('WebSocket connection established');
        reconnectAttempts = 0;
    };

    socket.onmessage = function(event) {
        const data = JSON.parse(event.data);
        updateBreakpointsUI(data);
    };

    socket.onclose = function(event) {
        console.log('WebSocket connection closed', event);

        // Try to reconnect if the connection closes unexpectedly
        if (reconnectAttempts < maxReconnectAttempts) {
            reconnectAttempts++;
            console.log(`Attempting to reconnect (${reconnectAttempts}/${maxReconnectAttempts})...`);
            setTimeout(connectWebSocket, reconnectDelay);
        } else {
            console.error('Maximum reconnect attempts reached. Please refresh the page.');
        }
    };

    socket.onerror = function(error) {
        console.error('WebSocket error:', error);
    };
}

/** Function to update the UI with breakpoint data */
function updateBreakpointsUI(data) {
    currentBreakpoints = data.breakpoints;
    document.getElementById('breakpoint-count').textContent = data.breakpointCount;

    // Apply filters before rendering
    const searchTerm = document.getElementById('search-input').value.toLowerCase();
    const typeFilter = document.getElementById('type-filter').value;
    const showDisabled = document.getElementById('show-disabled-toggle').checked;

    const filteredBreakpoints = currentBreakpoints.filter(bp => {
        // Type filter
        if (typeFilter !== 'all' && bp.type !== typeFilter) {
            return false;
        }

        // Disabled filter
        if (!showDisabled && !bp.enabled) {
            return false;
        }

        // Search filter
        if (searchTerm) {
            const filePath = bp.filePath ? bp.filePath.toLowerCase() : '';
            const descriptor = bp.descriptor ? bp.descriptor.toLowerCase() : '';
            return filePath.includes(searchTerm) || descriptor.includes(searchTerm) ||
                (bp.lineNumber && bp.lineNumber.toString().includes(searchTerm));
        }

        return true;
    });

    renderBreakpointTable(filteredBreakpoints);

    // Show empty state if no breakpoints after filtering
    const emptyState = document.getElementById('empty-state');
    if (filteredBreakpoints.length === 0) {
        emptyState.style.display = 'flex';
    } else {
        emptyState.style.display = 'none';
    }

    // If the selected breakpoint is still in the list, keep it selected
    if (selectedBreakpoint) {
        const stillExists = filteredBreakpoints.some(bp => bp.id === selectedBreakpoint.id);
        if (!stillExists) {
            hideDetailPanel();
        }
    }
}

function renderBreakpointTable(breakpoints) {
    const tableBody = document.getElementById('breakpoints-body');
    tableBody.innerHTML = '';

    breakpoints.forEach(bp => {
        const row = document.createElement('tr');
        row.dataset.id = bp.id;

        if (!bp.enabled) {
            row.classList.add('disabled-row');
        }

        // Selected state
        if (selectedBreakpoint && bp.id === selectedBreakpoint.id) {
            row.classList.add('selected');
        }

        // Status indicator column
        const statusCell = document.createElement('td');
        const statusIndicator = document.createElement('span');
        statusIndicator.className = `status-indicator ${!bp.enabled ? 'status-disabled' : ''}`;
        statusCell.appendChild(statusIndicator);
        row.appendChild(statusCell);

        // Type column
        const typeCell = document.createElement('td');
        const typeBadge = document.createElement('span');
        typeBadge.className = `type-badge type-${bp.type}`;
        typeBadge.textContent = bp.type.charAt(0) + bp.type.slice(1).toLowerCase();
        typeCell.appendChild(typeBadge);
        row.appendChild(typeCell);

        // Class column
        const classCell = document.createElement('td');
        classCell.textContent = bp.descriptor || 'N/A';
        row.appendChild(classCell);

        // File column with path formatting and navigation
        const fileCell = document.createElement('td');
        if (bp.filePath) {
            const filePathDiv = document.createElement('div');
            filePathDiv.className = 'file-path';

            // Extract filename and directory path
            const pathParts = bp.filePath.split('/');
            const fileName = pathParts.pop();
            const dirPath = pathParts.join('/');

            const fileNameSpan = document.createElement('span');
            fileNameSpan.className = 'file-name';
            fileNameSpan.textContent = fileName;

            // Add navigation icon to indicate it's clickable
            const navIcon = document.createElement('span');
            navIcon.className = 'navigate-icon';
            navIcon.textContent = '↗';
            fileNameSpan.appendChild(navIcon);

            filePathDiv.appendChild(fileNameSpan);

            if (dirPath) {
                const dirSpan = document.createElement('span');
                dirSpan.className = 'file-dir';
                dirSpan.textContent = ` (${dirPath})`;
                filePathDiv.appendChild(dirSpan);
            }

            // Add click handler for navigation
            filePathDiv.addEventListener('click', (event) => {
                event.stopPropagation(); // Prevent row selection
                navigateToBreakpoint(bp);
            });

            fileCell.appendChild(filePathDiv);
        } else {
            fileCell.textContent = 'Unknown';
        }
        row.appendChild(fileCell);

        // Line column with navigation
        const lineCell = document.createElement('td');
        if (bp.lineNumber) {
            const lineSpan = document.createElement('span');
            lineSpan.className = 'line-number';
            lineSpan.textContent = bp.lineNumber;

            // Add click handler for navigation
            lineSpan.addEventListener('click', (event) => {
                event.stopPropagation(); // Prevent row selection
                navigateToBreakpoint(bp);
            });

            lineCell.appendChild(lineSpan);
        } else {
            lineCell.textContent = 'N/A';
        }
        row.appendChild(lineCell);

        // Add click event to select breakpoint (but not navigate)
        row.addEventListener('click', () => {
            selectBreakpoint(bp);
        });

        tableBody.appendChild(row);
    });
}

function navigateToBreakpoint(breakpoint) {
    if (breakpoint.filePath && breakpoint.lineNumber && window.goToBreakpoint) {
        const link = `${breakpoint.filePath}|${breakpoint.lineNumber}`;
        window.goToBreakpoint(link);

        // Also select the breakpoint to show details
        selectBreakpoint(breakpoint);
    }
}

function selectBreakpoint(breakpoint) {
    selectedBreakpoint = breakpoint;

    // Update selected row styling
    const allRows = document.querySelectorAll('#breakpoints-body tr');
    allRows.forEach(row => row.classList.remove('selected'));
    const selectedRow = document.querySelector(`tr[data-id="${breakpoint.id}"]`);
    if (selectedRow) {
        selectedRow.classList.add('selected');
    }

    // Update detail panel
    document.getElementById('detail-type').textContent = breakpoint.type;
    document.getElementById('detail-descriptor').textContent = breakpoint.descriptor || 'N/A';
    document.getElementById('detail-file').textContent = breakpoint.filePath || 'N/A';
    document.getElementById('detail-line').textContent = breakpoint.lineNumber || 'N/A';
    document.getElementById('detail-status').textContent = breakpoint.enabled ? 'Enabled' : 'Disabled';

    // Show detail panel
    document.getElementById('details-panel').style.display = 'block';
}

function hideDetailPanel() {
    document.getElementById('details-panel').style.display = 'none';
    selectedBreakpoint = null;

    // Remove selected styling
    const allRows = document.querySelectorAll('#breakpoints-body tr');
    allRows.forEach(row => row.classList.remove('selected'));
}

// Fallback to REST API if WebSocket fails
function fetchBreakpoints() {
    fetch('/api/breakpoints')
        .then(response => response.json())
        .then(data => {
            updateBreakpointsUI(data);
        })
        .catch(error => console.error('Error fetching breakpoints:', error));
}

/** Set up event listeners for search and filters */
function setupListeners() {
    document.getElementById('search-input').addEventListener('input', () => {
        if (currentBreakpoints.length > 0) {
            updateBreakpointsUI({ breakpointCount: currentBreakpoints.length, breakpoints: currentBreakpoints });
        }
    });

    document.getElementById('type-filter').addEventListener('change', () => {
        if (currentBreakpoints.length > 0) {
            updateBreakpointsUI({ breakpointCount: currentBreakpoints.length, breakpoints: currentBreakpoints });
        }
    });

    document.getElementById('show-disabled-toggle').addEventListener('change', () => {
        if (currentBreakpoints.length > 0) {
            updateBreakpointsUI({ breakpointCount: currentBreakpoints.length, breakpoints: currentBreakpoints });
        }
    });

    document.getElementById('close-details').addEventListener('click', () => {
        hideDetailPanel();
    });

    document.getElementById('navigate-button').addEventListener('click', () => {
        if (selectedBreakpoint) {
            navigateToBreakpoint(selectedBreakpoint);
        }
    });
}

/** Set theme based on url parameter */
function setTheme() {
    const urlParams = new URLSearchParams(window.location.search);
    const theme = urlParams.get('theme') || 'light';

    document.body.classList.add(`theme-${theme}`);
}

document.addEventListener('DOMContentLoaded', function() {
    setTheme();
    connectWebSocket();
    setupListeners();

    // Fallback to REST API if WebSocket connection fails
    setTimeout(() => {
        if (socket.readyState !== WebSocket.OPEN) {
            console.log('WebSocket connection not established. Falling back to REST API');
            fetchBreakpoints();
        }
    }, 3000); // Give WebSocket 3 seconds to connect
});

// Reconnect on page visibility change (user returns to tab)
document.addEventListener('visibilitychange', function() {
    if (document.visibilityState === 'visible' && (!socket || socket.readyState !== WebSocket.OPEN)) {
        connectWebSocket();
    }
});
