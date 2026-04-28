package screens.customer;

import components.*;
import utils.AppColors;
import utils.AppFonts;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * EMI Calculator - interactive sliders for Loan Amount, Interest Rate, Tenure.
 * Displays monthly EMI, principal, interest, and total payable.
 */
public class EMICalculatorScreen extends JPanel {
    private JSlider amountSlider;
    private JSlider rateSlider;
    private JSlider tenureSlider;
    private JLabel amountValueLabel;
    private JLabel rateValueLabel;
    private JLabel tenureValueLabel;
    private JLabel emiValueLabel;
    private JLabel principalLabel;
    private JLabel interestLabel;
    private JLabel totalLabel;
    private JPanel breakdownBar;

    public EMICalculatorScreen() {
        setBackground(AppColors.BG_MAIN);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        buildUI();
        updateCalculation();
    }

    private void buildUI() {
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        // Title
        JLabel title = new JLabel("EMI Calculator");
        title.setFont(AppFonts.TITLE_LARGE);
        title.setForeground(AppColors.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(5));

        JLabel subtitle = new JLabel("Calculate your monthly EMI for different loan amounts and tenures.");
        subtitle.setFont(AppFonts.SUBTITLE);
        subtitle.setForeground(AppColors.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(subtitle);
        content.add(Box.createVerticalStrut(25));

        // Main content: 2 columns
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        mainPanel.setOpaque(false);
        mainPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Left column: sliders
        mainPanel.add(createSlidersPanel());

        // Right column: results
        mainPanel.add(createResultsPanel());

        content.add(mainPanel);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(AppColors.BG_MAIN);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    private RoundedPanel createSlidersPanel() {
        RoundedPanel panel = new RoundedPanel(14);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headerPanel.setOpaque(false);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel icon = new JLabel("\u2610");
        icon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        icon.setForeground(AppColors.PRIMARY_BLUE);
        headerPanel.add(icon);
        JLabel headerLabel = new JLabel("Loan Details");
        headerLabel.setFont(AppFonts.HEADING);
        headerLabel.setForeground(AppColors.TEXT_PRIMARY);
        headerPanel.add(headerLabel);
        panel.add(headerPanel);
        panel.add(Box.createVerticalStrut(25));

        ChangeListener updateListener = e -> updateCalculation();

        // Loan Amount slider
        amountValueLabel = new JLabel("\u20B9500,000");
        amountSlider = createStyledSlider(50000, 10000000, 500000);
        amountSlider.addChangeListener(updateListener);
        panel.add(createSliderSection("Loan Amount", amountValueLabel, amountSlider, "\u20B950K", "\u20B91Cr"));
        panel.add(Box.createVerticalStrut(25));

        // Interest Rate slider
        rateValueLabel = new JLabel("8.5%");
        rateSlider = createStyledSlider(50, 200, 85); // values * 10
        rateSlider.addChangeListener(updateListener);
        panel.add(createSliderSection("Interest Rate (% p.a.)", rateValueLabel, rateSlider, "5%", "20%"));
        panel.add(Box.createVerticalStrut(25));

        // Tenure slider
        tenureValueLabel = new JLabel("120 months (10.0 years)");
        tenureSlider = createStyledSlider(12, 360, 120);
        tenureSlider.addChangeListener(updateListener);
        panel.add(createSliderSection("Loan Tenure (Months)", tenureValueLabel, tenureSlider, "1 Year", "30 Years"));

        return panel;
    }

    private JSlider createStyledSlider(int min, int max, int value) {
        JSlider slider = new JSlider(min, max, value);
        slider.setOpaque(false);
        slider.setForeground(AppColors.PRIMARY_BLUE);
        slider.setPreferredSize(new Dimension(300, 30));
        return slider;
    }

    private JPanel createSliderSection(String label, JLabel valueLabel, JSlider slider, String minLabel, String maxLabel) {
        JPanel section = new JPanel();
        section.setOpaque(false);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Label row with value
        JPanel labelRow = new JPanel(new BorderLayout());
        labelRow.setOpaque(false);
        labelRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        labelRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JLabel lbl = new JLabel(label);
        lbl.setFont(AppFonts.BODY);
        lbl.setForeground(AppColors.TEXT_SECONDARY);
        labelRow.add(lbl, BorderLayout.WEST);

        valueLabel.setFont(AppFonts.BODY_BOLD);
        valueLabel.setForeground(AppColors.PRIMARY_BLUE);
        labelRow.add(valueLabel, BorderLayout.EAST);

        section.add(labelRow);
        section.add(Box.createVerticalStrut(8));

        // Slider
        slider.setAlignmentX(Component.LEFT_ALIGNMENT);
        slider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        section.add(slider);
        section.add(Box.createVerticalStrut(3));

        // Min/Max labels
        JPanel rangePanel = new JPanel(new BorderLayout());
        rangePanel.setOpaque(false);
        rangePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rangePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));

        JLabel minLbl = new JLabel(minLabel);
        minLbl.setFont(AppFonts.TINY);
        minLbl.setForeground(AppColors.TEXT_MUTED);
        rangePanel.add(minLbl, BorderLayout.WEST);

        JLabel maxLbl = new JLabel(maxLabel);
        maxLbl.setFont(AppFonts.TINY);
        maxLbl.setForeground(AppColors.TEXT_MUTED);
        rangePanel.add(maxLbl, BorderLayout.EAST);

        section.add(rangePanel);

        return section;
    }

    private RoundedPanel createResultsPanel() {
        RoundedPanel panel = new RoundedPanel(14);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel header = new JLabel("EMI Breakdown");
        header.setFont(AppFonts.HEADING);
        header.setForeground(AppColors.TEXT_PRIMARY);
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(header);
        panel.add(Box.createVerticalStrut(20));

        // Monthly EMI
        RoundedPanel emiCard = new RoundedPanel(10);
        emiCard.setBackground(new Color(239, 246, 255));
        emiCard.setLayout(new BoxLayout(emiCard, BoxLayout.Y_AXIS));
        emiCard.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        emiCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        emiCard.setHasShadow(false);
        emiCard.setBorderColor(new Color(191, 219, 254));

        JLabel emiLabel = new JLabel("Monthly EMI");
        emiLabel.setFont(AppFonts.BODY);
        emiLabel.setForeground(AppColors.PRIMARY_BLUE);
        emiLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        emiCard.add(emiLabel);

        emiValueLabel = new JLabel("\u20B96,199");
        emiValueLabel.setFont(AppFonts.CARD_VALUE);
        emiValueLabel.setForeground(AppColors.PRIMARY_BLUE);
        emiValueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        emiCard.add(emiValueLabel);

        panel.add(emiCard);
        panel.add(Box.createVerticalStrut(20));

        // Principal & Interest
        JPanel piRow = new JPanel(new GridLayout(1, 2, 15, 0));
        piRow.setOpaque(false);
        piRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        piRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        principalLabel = new JLabel("\u20B9500,000");
        interestLabel = new JLabel("\u20B9243,914");

        piRow.add(createMiniCard("Principal Amount", principalLabel, AppColors.TEXT_PRIMARY));
        piRow.add(createMiniCard("Total Interest", interestLabel, AppColors.CHART_ORANGE));

        panel.add(piRow);
        panel.add(Box.createVerticalStrut(20));

        // Total Payable
        RoundedPanel totalCard = new RoundedPanel(10);
        totalCard.setBackground(new Color(240, 253, 244));
        totalCard.setLayout(new BoxLayout(totalCard, BoxLayout.Y_AXIS));
        totalCard.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        totalCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        totalCard.setHasShadow(false);
        totalCard.setBorderColor(new Color(187, 247, 208));

        JLabel totalTitleLabel = new JLabel("Total Amount Payable");
        totalTitleLabel.setFont(AppFonts.BODY);
        totalTitleLabel.setForeground(AppColors.STATUS_GREEN);
        totalTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        totalCard.add(totalTitleLabel);

        totalLabel = new JLabel("\u20B9743,914");
        totalLabel.setFont(AppFonts.CARD_VALUE);
        totalLabel.setForeground(AppColors.STATUS_GREEN);
        totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        totalCard.add(totalLabel);

        panel.add(totalCard);
        panel.add(Box.createVerticalStrut(25));

        // Payment Breakdown bar
        JLabel breakdownTitle = new JLabel("Payment Breakdown");
        breakdownTitle.setFont(AppFonts.HEADING);
        breakdownTitle.setForeground(AppColors.TEXT_PRIMARY);
        breakdownTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(breakdownTitle);
        panel.add(Box.createVerticalStrut(10));

        breakdownBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();

                double principal = amountSlider.getValue();
                double rate = rateSlider.getValue() / 10.0 / 12.0 / 100.0;
                int tenure = tenureSlider.getValue();
                double emi = principal * rate * Math.pow(1 + rate, tenure) / (Math.pow(1 + rate, tenure) - 1);
                double totalAmt = emi * tenure;
                double interest = totalAmt - principal;
                double principalPct = principal / totalAmt;

                int principalWidth = (int) (w * principalPct);

                // Principal part (blue)
                g2.setColor(AppColors.PRIMARY_BLUE);
                g2.fill(new RoundRectangle2D.Double(0, 0, principalWidth, h, 10, 10));

                // Interest part (orange)
                g2.setColor(AppColors.CHART_ORANGE);
                g2.fill(new RoundRectangle2D.Double(principalWidth, 0, w - principalWidth, h, 10, 10));

                // Text labels
                g2.setColor(Color.WHITE);
                g2.setFont(AppFonts.BODY_BOLD);
                FontMetrics fm = g2.getFontMetrics();

                String pctPrincipal = String.format("%.0f%%", principalPct * 100);
                String pctInterest = String.format("%.0f%%", (1 - principalPct) * 100);

                if (principalWidth > 40) {
                    g2.drawString(pctPrincipal, principalWidth / 2 - fm.stringWidth(pctPrincipal) / 2, h / 2 + fm.getAscent() / 2 - 2);
                }
                int interestX = principalWidth + (w - principalWidth) / 2 - fm.stringWidth(pctInterest) / 2;
                if (w - principalWidth > 40) {
                    g2.drawString(pctInterest, interestX, h / 2 + fm.getAscent() / 2 - 2);
                }

                g2.dispose();
            }
        };
        breakdownBar.setPreferredSize(new Dimension(0, 36));
        breakdownBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        breakdownBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(breakdownBar);
        panel.add(Box.createVerticalStrut(10));

        // Legend
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        legend.setOpaque(false);
        legend.setAlignmentX(Component.LEFT_ALIGNMENT);

        legend.add(createLegendItem("\u25CF  Principal", AppColors.PRIMARY_BLUE));
        legend.add(createLegendItem("\u25CF  Interest", AppColors.CHART_ORANGE));

        panel.add(legend);

        return panel;
    }

    private JPanel createMiniCard(String label, JLabel valueLabel, Color valueColor) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER, 1, true),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        JLabel lbl = new JLabel(label);
        lbl.setFont(AppFonts.SMALL);
        lbl.setForeground(AppColors.TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(5));

        valueLabel.setFont(AppFonts.HEADING);
        valueLabel.setForeground(valueColor);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(valueLabel);

        return panel;
    }

    private JPanel createLegendItem(String text, Color color) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        JLabel label = new JLabel(text);
        label.setFont(AppFonts.SMALL);
        label.setForeground(color);
        panel.add(label);
        return panel;
    }

    private void updateCalculation() {
        double principal = amountSlider.getValue();
        double annualRate = rateSlider.getValue() / 10.0;
        double monthlyRate = annualRate / 12.0 / 100.0;
        int tenure = tenureSlider.getValue();

        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("en", "IN"));

        amountValueLabel.setText("\u20B9" + nf.format((long) principal));
        rateValueLabel.setText(String.format("%.1f%%", annualRate));
        tenureValueLabel.setText(tenure + " months (" + String.format("%.1f", tenure / 12.0) + " years)");

        if (monthlyRate > 0 && tenure > 0) {
            double emi = principal * monthlyRate * Math.pow(1 + monthlyRate, tenure) / (Math.pow(1 + monthlyRate, tenure) - 1);
            double totalPayable = emi * tenure;
            double totalInterest = totalPayable - principal;

            emiValueLabel.setText("\u20B9" + nf.format((long) emi));
            principalLabel.setText("\u20B9" + nf.format((long) principal));
            interestLabel.setText("\u20B9" + nf.format((long) totalInterest));
            totalLabel.setText("\u20B9" + nf.format((long) totalPayable));
        }

        if (breakdownBar != null) breakdownBar.repaint();
    }
}
