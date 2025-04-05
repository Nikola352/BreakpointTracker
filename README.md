# BreakpointTracker

A powerful IntelliJ plugin that provides a comprehensive overview of all breakpoints in your project.

## Features
- **Complete Breakpoint Overview**: View all breakpoints across your project in a dedicated tool window
- **Multi-type Support**: Works with all breakpoint types (line, method, field, exception)
- **Quick Navigation**: Click on any breakpoint to open its location in the editor
- **Real-time Updates**: Changes to breakpoints are immediately reflected in the overview
- **Theming Support**: Fully compatible with both light and dark IDE themes

## Installation

You can install the plugin using one of two methods:

### Option 1: Install from ZIP

1. Download the ZIP archive from the [releases section](https://github.com/Nikola352/BreakpointTracker/releases)
2. In IntelliJ IDEA, go to Settings/Preferences → Plugins → ⚙️ → Install Plugin from Disk...
3. Select the downloaded ZIP file

### Option 2: Run from Source

1. Clone the repository
2. Open the project in IntelliJ IDEA
3. Run the plugin using `./gradlew runIde`

How It Works
The BreakpointTracker tool window appears at the bottom of your IDE, displaying a table of all breakpoints in your project. Each row shows:

- Breakpoint type (with color codes)
- File path
- Line number
- A descriptor specifying the breakpoint pattern

The overview updates in real time as you add, remove, or hit breakpoints during debugging sessions.

## Architecture

The plugin is built on a modern, efficient architecture:

- **Optimized Data Storage**: Uses thread-safe concurrent data structures (ConcurrentSkipListSet and ConcurrentHashMap) for reliable breakpoint tracking, even during intensive debugging sessions
- **Event-driven Communication**: Leverages IntelliJ Platform's MessageBus system for seamless component communication
- **Embedded Browser UI**: Utilizes JCEF (Chromium Embedded Framework) for a responsive, modern interface
- **Dedicated Backend**: Separate Ktor module serves the frontend with minimal overhead
- **WebSocket Communication**: Real-time updates between the UI and the plugin core

## Contributing
Contributions are welcome! Feel free to submit issues or pull requests.

## License
This project is licensed under the MIT License - see the [LICENSE](https://github.com/Nikola352/BreakpointTracker/blob/main/LICENSE) file for details.
