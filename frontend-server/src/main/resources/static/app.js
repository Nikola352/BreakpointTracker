// Fetch breakpoints data from the server
function fetchBreakpoints() {
    fetch('/api/breakpoints')
        .then(response => response.json())
        .then(data => {
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
        })
        .catch(error => console.error('Error fetching breakpoints:', error));
}

// Initial fetch
fetchBreakpoints();

// Poll for updates every 2 seconds
setInterval(fetchBreakpoints, 2000);