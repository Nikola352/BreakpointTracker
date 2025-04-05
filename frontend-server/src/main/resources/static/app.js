let socket = null;
let reconnectAttempts = 0;
const maxReconnectAttempts = 5;
const reconnectDelay = 2000; // 2 seconds

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

// Function to update the UI with breakpoint data
function updateBreakpointsUI(data) {
    document.getElementById('breakpoint-count').textContent = data.totalCount;
    const tableBody = document.getElementById('breakpoints-body');
    tableBody.innerHTML = '';

    data.breakpoints.forEach(bp => {
        const row = document.createElement('tr');

        const fileCell = document.createElement('td');
        fileCell.textContent = bp.filePath || 'Unknown';
        row.appendChild(fileCell);

        const lineCell = document.createElement('td');
        lineCell.textContent = bp.lineNumber || 'N/A';
        row.appendChild(lineCell);

        tableBody.appendChild(row);
    });
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

document.addEventListener('DOMContentLoaded', function() {
    connectWebSocket();

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
