#🎱 Billiard Simulation (BallAnimation.java)
#🧩 Overview

Billiard Simulation is a Java Swing application that visually simulates a billiard table with multiple moving balls.
The project demonstrates object-oriented programming, 2D graphics, basic physics (collision detection, reflection, deceleration), and interactive UI controls.

It allows users to:

Choose the number of balls.

Select a table map (hole layout).

Predict which ball will fall into a hole first.

Start, reset, and reapply parameters dynamically through a control panel.

#💻 Programming Language

Language: Java

Framework: Java Swing (AWT-based GUI)

JDK version: 17 or higher (recommended: OpenJDK 17–25)

#⚙️ Features
Feature	Description
#🎨 Real-time Animation	Balls move and collide naturally using physics-based reflection and deceleration.
#🕳️ Multiple Table Maps	3 different map layouts with unique hole positions and colors.
#🔢 Ball Prediction System	You can guess which ball number will fall first — the program shows whether your prediction is correct.
#🧮 Collision Handling	Includes realistic bouncing between balls and table boundaries.
#🖱️ Interactive Clicks	Click any ball to give it a new random direction and speed.
#🔁 Dynamic Control Panel	Change number of balls, map type, and predicted ball without restarting the app.
#🧠 How It Works

Each ball is an object with position, velocity, and radius.

The program uses a Timer to update ball positions every 20 ms.

When a ball touches a hole, it disappears.

The first ball that falls triggers a dialog message:

#✅ If it matches your prediction → “Correct!”

#❌ Otherwise → “Wrong guess!”

#▶️ How to Run
Option 1: From IntelliJ IDEA / Eclipse

Open the project folder b4_update in your IDE.

Ensure the JDK is set (at least version 17).

Navigate to:

src/b4_update/BallAnimation.java


Right-click → Run 'BallAnimation.main()'

Option 2: From Command Line
cd b4_update/src
javac b4_update/BallAnimation.java
java b4_update.BallAnimation

#🧭 Controls and Usage
Control	Function
Số bóng (Number of Balls)	Choose how many balls will appear.
Chọn Map (Choose Map)	Select table layout (1, 2, or 3).
Bóng dự đoán (Predicted Ball)	Enter which ball number you think will fall first.
Áp dụng (Apply)	Save settings.
Reset	Reset balls and table.
Chạy (Start)	Begin simulation and launch all balls.
#🌈 Example

Set number of balls to 20.

Choose Map 2.

Predict ball #5.

Click Start → balls scatter.

When the first ball drops into a hole, a popup tells if your prediction was right.

#🧰 Technical Notes

Uses javax.swing.Timer for consistent animation updates.

Applies velocity damping (SLOW_FACTOR) to slow balls gradually.

Each map defines hole coordinates and table colors.

Safe UI updates through SwingUtilities.invokeLater() for dialog boxes.

🧑‍💻 Author

Trinh Quang Vinh 

Video : https://www.youtube.com/watch?v=b-6P8e0YqGA
