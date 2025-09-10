# Thread Management System

This project is a queue simulation system implemented in Java, designed to model and visualize how clients are distributed across multiple servers. It demonstrates concepts of concurrency, scheduling strategies, and real-time simulation through a graphical user interface.

## Features
- Simulation of multiple servers processing client requests concurrently.
- Configurable parameters: number of clients, servers, simulation interval, arrival time, and service time.
- Two scheduling strategies: shortest queue (assigns clients to the server with the fewest tasks), and shortest waiting time (assigns clients to the server with the lowest estimated processing time).
- Real-time JavaFx GUI displaying waiting clients, server queues, and simulation progress.
- Automatic calculation of average waiting time, average service time, and peak hour.
- Logging of simulation events to both the GUI and a log file.

## Technologies Used
- **Language:** Java
- **Frameworks & Tools:** JavaFX, IntelliJ IDEA
- **Concepts Used:** Multithreading, Concurrency, Scheduling Algorithms, GUI Development

## How It Works
Clients are generated with random arrival and service times within a defined range. A scheduler assigns them to servers based on the chisen strategy. Each server runs on a separate thread, decrementing service times in real time. The GUI displays the system state dynamically, while metrics are tracked and shown at the end of the simulation.

## Future Improvements
- Enhance the GUI for better vizualization (e.g., charts or timeline views).
- Add more scheduling strategies.
- Support saving and loading simulation configurations.
