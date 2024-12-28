import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;

public class LiftSimulation extends JFrame {
    private final JButton[] lift1Buttons = new JButton[10];
    private final JButton[] lift2Buttons = new JButton[10];
    private final JLabel lift1StatusLabel = new JLabel("Lift 1 at Floor: 1");
    private final JLabel lift2StatusLabel = new JLabel("Lift 2 at Floor: 1");
    private int currentFloor1 = 1; // Initial position of lift 1
    private int currentFloor2 = 1; // Initial position of lift 2
    private int direction1 = 0; // 0 = IDLE, 1 = UP, -1 = DOWN for lift 1
    private int direction2 = 0; // 0 = IDLE, 1 = UP, -1 = DOWN for lift 2
    private final List<Integer> upQueue1 = new ArrayList<>();
    private final List<Integer> downQueue1 = new ArrayList<>();
    private final List<Integer> upQueue2 = new ArrayList<>();
    private final List<Integer> downQueue2 = new ArrayList<>();
    private boolean doorsOpen1 = false; // Track door state for lift 1
    private boolean doorsOpen2 = false; // Track door state for lift 2

    public LiftSimulation() {
        setTitle("Lift Simulation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 600);
        setLayout(new BorderLayout());

        // Status Panel for Lift 1
        // Create a panel for the top section to hold both status panels
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        // Status Panel for Lift 1 (left side at the top)
        JPanel statusPanel1 = new JPanel();
        statusPanel1.setLayout(new FlowLayout(FlowLayout.LEFT)); // Align to the left
        lift1StatusLabel.setFont(new Font("Monserrat", Font.BOLD, 16));
        statusPanel1.add(lift1StatusLabel);
        statusPanel1.setBorder(BorderFactory.createEmptyBorder(10, 30, 0, 0));
        topPanel.add(statusPanel1, BorderLayout.WEST); // Place at the left side of the top panel

        // Status Panel for Lift 2 (right side at the top)
        JPanel statusPanel2 = new JPanel();
        statusPanel2.setLayout(new FlowLayout(FlowLayout.RIGHT)); // Align to the right
        lift2StatusLabel.setFont(new Font("Monserrat", Font.BOLD, 16));
        statusPanel2.add(lift2StatusLabel);
        statusPanel2.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 30));
        topPanel.add(statusPanel2, BorderLayout.EAST); // Place at the right side of the top panel

        // Add the top panel to the main frame
        add(topPanel, BorderLayout.NORTH);

        // Panel for floor buttons
        JPanel floorPanel = new JPanel(new GridLayout(10, 3, 5, 10));
        for (int i = 10; i > 0; i--) {
            int floor = i;

            // Create buttons
            JButton upButton = new JButton("\u2191"); // Unicode for up arrow
            JButton downButton = new JButton("\u2193"); // Unicode for down arrow

            // Add action listeners
            if (floor < 10) {
                upButton.addActionListener(e -> handleFloorRequest(floor, 1));
            } else {
                upButton.setEnabled(false); // Disable for the top floor
            }

            if (floor > 1) {
                downButton.addActionListener(e -> handleFloorRequest(floor, -1));
            } else {
                downButton.setEnabled(false); // Disable for the ground floor
            }

            // Add buttons and label
            JLabel floorLabel = new JLabel("Floor " + floor, SwingConstants.CENTER);
            floorPanel.setBorder(BorderFactory.createEmptyBorder(5, 100, 10, 100)); // Add padding to the left
            floorPanel.add(upButton);
            floorPanel.add(floorLabel);
            floorPanel.add(downButton);
        }
        add(floorPanel, BorderLayout.CENTER);

        // Panel for lift control buttons (Lift 1)
        JPanel lift1Panel = new JPanel(new GridLayout(10, 1, 10, 10));
        for (int i = 0; i < 10; i++) {
            int floor = i + 1;
            lift1Buttons[i] = new JButton(String.valueOf(floor));
            lift1Buttons[i].setBackground(null);
            lift1Buttons[i].addActionListener(e -> handleLift1Request(floor));
            lift1Panel.add(lift1Buttons[i]);
        }
        lift1Panel.setBorder(BorderFactory.createEmptyBorder(5, 70, 10, 0)); // Add padding to the left
        add(lift1Panel, BorderLayout.WEST);

        // Panel for lift control buttons (Lift 2)
        JPanel lift2Panel = new JPanel(new GridLayout(10, 1, 10, 10));
        for (int i = 0; i < 10; i++) {
            int floor = i + 1;
            lift2Buttons[i] = new JButton(String.valueOf(floor));
            lift2Buttons[i].setBackground(null);
            lift2Buttons[i].addActionListener(e -> handleLift2Request(floor));
            lift2Panel.add(lift2Buttons[i]);
        }
        lift2Panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 70)); // Add padding to the right
        add(lift2Panel, BorderLayout.EAST);

        // Start simulation timer
        Timer timer1 = new Timer(1000, e -> simulateLift1Movement());
        Timer timer2 = new Timer(1000, e -> simulateLift2Movement());
        timer1.start();
        timer2.start();

        setVisible(true);
        setLocationRelativeTo(null);
    }

    private void handleFloorRequest(int floor, int requestDirection) {
        if (requestDirection == 1 && !upQueue1.contains(floor)) {
            upQueue1.add(floor);
            Collections.sort(upQueue1); // Ensure ascending order
        } else if (requestDirection == -1 && !downQueue1.contains(floor)) {
            downQueue1.add(floor);
            Collections.sort(downQueue1, Collections.reverseOrder()); // Ensure descending order
        }

        if (requestDirection == 1 && !upQueue2.contains(floor)) {
            upQueue2.add(floor);
            Collections.sort(upQueue2); // Ensure ascending order
        } else if (requestDirection == -1 && !downQueue2.contains(floor)) {
            downQueue2.add(floor);
            Collections.sort(downQueue2, Collections.reverseOrder()); // Ensure descending order
        }
    }

    private void handleLift1Request(int floor) {
        if (!upQueue1.contains(floor) && !downQueue1.contains(floor)) {
            lift1Buttons[floor - 1].setBackground(Color.BLUE);
            lift1Buttons[floor - 1].setForeground(Color.WHITE);
            if (floor > currentFloor1) {
                upQueue1.add(floor);
                Collections.sort(upQueue1);
            } else if (floor < currentFloor1) {
                downQueue1.add(floor);
                Collections.sort(downQueue1, Collections.reverseOrder());
            }
        }
    }

    private void handleLift2Request(int floor) {
        if (!upQueue2.contains(floor) && !downQueue2.contains(floor)) {
            lift2Buttons[floor - 1].setBackground(Color.BLUE);
            lift2Buttons[floor - 1].setForeground(Color.WHITE);
            if (floor > currentFloor2) {
                upQueue2.add(floor);
                Collections.sort(upQueue2);
            } else if (floor < currentFloor2) {
                downQueue2.add(floor);
                Collections.sort(downQueue2, Collections.reverseOrder());
            }
        }
    }

    private void simulateLift1Movement() {
        if (doorsOpen1) {
            return; // Wait if doors are open
        }

        // Simulate Lift 1 movement
        if (direction1 == 0) { // Idle state for lift 1
            if (!upQueue1.isEmpty()) {
                direction1 = 1; // Go UP
            } else if (!downQueue1.isEmpty()) {
                direction1 = -1; // Go DOWN
            } else {
                lift1StatusLabel.setText("Lift 1 at Floor: " + currentFloor1);
                return; // No requests
            }
        }

        // Determine next target floor1 based on direction
        Integer targetFloor1 = null;
        if (direction1 == 1) {
            targetFloor1 = upQueue1.isEmpty() ? null : Collections.min(upQueue1);
        } else if (direction1 == -1) {
            targetFloor1 = downQueue1.isEmpty() ? null : Collections.max(downQueue1);
        }

        if (targetFloor1 != null && currentFloor1 == targetFloor1) {
            // Lift reaches target floor
            if (direction1 == 1) {
                upQueue1.remove(targetFloor1);
            } else {
                downQueue1.remove(targetFloor1);
            }
            lift1StatusLabel.setText("Lift stopped at Floor: " + currentFloor1 + " (Doors Open)");
            doorsOpen1 = true;
            Timer doorTimer1 = new Timer(5000, e -> {
                lift1StatusLabel.setText("Doors Closed");
                doorsOpen1 = false;
                lift1Buttons[currentFloor1 - 1].setBackground(null);
                lift1Buttons[currentFloor1 - 1].setForeground(null);
            });
            doorTimer1.setRepeats(false);
            doorTimer1.start();
            return;
        }

        // Move lift1 if there is a valid target floor
        if (targetFloor1 != null) {
            if (currentFloor1 < targetFloor1) {
                currentFloor1++;
            } else if (currentFloor1 > targetFloor1) {
                currentFloor1--;
            }
            lift1StatusLabel.setText("Lift moving " + (direction1 == 1 ? "\\u2191" : "\\u2193") + " to Floor: " + currentFloor1);
        }

        // If no more requests in the current direction, set to idle
        if (direction1 == 1 && upQueue1.isEmpty() || direction1 == -1 && downQueue1.isEmpty()) {
            direction1 = 0;
        }

        // Update the status labels
        lift1StatusLabel.setText("Lift 1 at Floor: " + currentFloor1);
    }
    
    private void simulateLift2Movement() {
        if (doorsOpen2) {
            return; // Wait if doors are open
        }

        // Simulate Lift 2 movement
        if (direction2 == 0) { // Idle state for lift 2
            if (!upQueue2.isEmpty()) {
                direction2 = 1; // Go UP
            } else if (!downQueue2.isEmpty()) {
                direction2 = -1; // Go DOWN
            } else {
                lift2StatusLabel.setText("Lift 2 at Floor: " + currentFloor2);
                return; // No requests
            }
        }

        // Determine next target floor2 based on direction
        Integer targetFloor2 = null;
        if (direction2 == 1) {
            targetFloor2 = upQueue2.isEmpty() ? null : Collections.min(upQueue2);
        } else if (direction2 == -1) {
            targetFloor2 = downQueue2.isEmpty() ? null : Collections.max(downQueue2);
        }

        if (targetFloor2 != null && currentFloor2 == targetFloor2) {
            // Lift reaches target floor
            if (direction2 == 1) {
                upQueue2.remove(targetFloor2);
            } else {
                downQueue2.remove(targetFloor2);
            }
            lift2StatusLabel.setText("Lift stopped at Floor: " + currentFloor2 + " (Doors Open)");
            doorsOpen2 = true;
            Timer doorTimer2 = new Timer(5000, e -> {
                lift2StatusLabel.setText("Doors Closed");
                doorsOpen2 = false;
                lift2Buttons[currentFloor2 - 1].setBackground(null);
                lift2Buttons[currentFloor2 - 1].setForeground(null);
            });
            doorTimer2.setRepeats(false);
            doorTimer2.start();
            return;
        }

        // Move lift2 if there is a valid target floor
        if (targetFloor2 != null) {
            if (currentFloor2 < targetFloor2) {
                currentFloor2++;
            } else if (currentFloor2 > targetFloor2) {
                currentFloor2--;
            }
            lift2StatusLabel.setText("Lift moving " + (direction2 == 1 ? "\\u2191" : "\\u2193") + " to Floor: " + currentFloor2);
        }

        // If no more requests in the current direction, set to idle
        if (direction2 == 1 && upQueue2.isEmpty() || direction2 == -1 && downQueue2.isEmpty()) {
            direction2 = 0;
        }

        // Update the status labels
        lift2StatusLabel.setText("Lift 2 at Floor: " + currentFloor2);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(LiftSimulation::new);
    }
}
    
