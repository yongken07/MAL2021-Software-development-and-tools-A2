package com.lms.desktop;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

/**
 * LMS Desktop Application with Usability Improvements
 */
public class LmsDesktopApp extends JFrame {
    private JTextField studentIdField;
    private JButton getEnrollmentsButton;
    private JTable enrollmentsTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LmsDesktopApp() {
        setTitle("LMS - Student Enrollments");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Top Panel: Search
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(new JLabel("Student ID:"));
        studentIdField = new JTextField(10);
        // Improvement: Hint text / Numeric constraint could be added here
        topPanel.add(studentIdField);
        
        getEnrollmentsButton = new JButton("Get Enrollments");
        topPanel.add(getEnrollmentsButton);

        add(topPanel, BorderLayout.NORTH);

        // Center Panel: Table
        String[] columnNames = {"Course ID", "Course Name", "Instructor"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Improvement: Make table read-only
            }
        };
        enrollmentsTable = new JTable(tableModel);
        enrollmentsTable.setFillsViewportHeight(true);
        add(new JScrollPane(enrollmentsTable), BorderLayout.CENTER);

        // Bottom Panel: Status Bar (Improvement: Visual Feedback)
        statusLabel = new JLabel("Ready");
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBorder(BorderFactory.createBevelBorder(1));
        bottomPanel.add(statusLabel);
        add(bottomPanel, BorderLayout.SOUTH);

        // Button Action
        getEnrollmentsButton.addActionListener(e -> fetchEnrollments());

        setLocationRelativeTo(null);
    }

    private void fetchEnrollments() {
        String studentId = studentIdField.getText().trim();
        
        // Improvement: Input Validation
        if (!studentId.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Student ID must be a numeric value.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Improvement: Loading State
        setLoading(true);
        tableModel.setRowCount(0); // Clear previous results immediately

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/v1/students/" + studentId + "/enrollments"))
                    .GET()
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        if (response.statusCode() == 404) throw new RuntimeException("Student not found");
                        return response.body();
                    })
                    .thenAccept(this::updateTable)
                    .exceptionally(ex -> {
                        SwingUtilities.invokeLater(() -> {
                            setLoading(false);
                            JOptionPane.showMessageDialog(this, "Request Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        });
                        return null;
                    });

        } catch (Exception e) {
            setLoading(false);
            JOptionPane.showMessageDialog(this, "Internal Error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setLoading(boolean loading) {
        getEnrollmentsButton.setEnabled(!loading);
        studentIdField.setEnabled(!loading);
        statusLabel.setText(loading ? "Fetching data... please wait." : "Ready");
        if (loading) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void updateTable(String json) {
        SwingUtilities.invokeLater(() -> {
            try {
                List<Map<String, Object>> enrollments = objectMapper.readValue(json, new TypeReference<>() {});
                
                if (enrollments.isEmpty()) {
                    statusLabel.setText("No enrollments found.");
                } else {
                    for (Map<String, Object> enrollment : enrollments) {
                        tableModel.addRow(new Object[]{
                                enrollment.get("id"),
                                enrollment.get("name"),
                                enrollment.get("instructorName")
                        });
                    }
                    statusLabel.setText("Found " + enrollments.size() + " enrollments.");
                }
                setLoading(false);
            } catch (Exception e) {
                setLoading(false);
                JOptionPane.showMessageDialog(this, "Data Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LmsDesktopApp().setVisible(true);
        });
    }
}
