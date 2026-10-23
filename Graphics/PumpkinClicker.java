import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class PumpkinClicker extends JPanel implements MouseListener 
{
    private long pumpkins = 0;
    private long pumpkinsPerSecond = 0;
    private long pumpkinsPerClick = 1;

    // Scale factor (Change this to make it larger or smaller, e.g., 1.25, 1.5, 2.0)
    private final double SCALE = 1.5;

    // Ascension requirements
    private final long req1 = 5_000_000;
    private final long req2 = 1_000_000_000;
    private final long req3 = 50_000_000_000L;

    private boolean asc1 = false;
    private boolean asc2 = false;
    private boolean gameOver = false;
    private boolean upgrades = false;

    // Building costs (dynamic)
    private long clickerCost = 15;
    private long farmerCost = 60;
    private long patchCost = 350;
    private long businessCost = 1500;
    private long hauntedCost = 7500;

    private long altarCost = 45000;
    private long templeCost = 300000;
    private long demonCost = 2500000;
    private long overlordCost = 20000000;
    private long godCost = 150000000;

    // Building Production Amounts
    private long clickerAmount = 1;
    private long farmAmount = 1;
    private long patchAmount = 5;
    private long businessAmount = 20;
    private long hauntedAmount = 50;
    
    private long altarAmount = 100;
    private long templeAmount = 500;
    private long demonAmount = 2500;
    private long overlordAmount = 7500;
    private long godAmount = 50000;

    // Buildings Owned
    private long clickers = 0;
    private long farms = 0;
    private long patches = 0;
    private long businesses = 0;
    private long haunteds = 0;

    private long altars = 0;
    private long temples = 0;
    private long demons = 0;
    private long overlords = 0;
    private long gods = 0;

    // Images
    private Image background1;
    private Image background2;
    private Image background3;
    private Image upgradeBackground;
    private Image pumpkinImage;

    private Timer timer;
    private boolean spacePressed = false;

    // Upgrade Backlog Lists
    private List<String> availableUpgrades = new ArrayList<>();
    private boolean click1Bought = false;
    private boolean farm1Bought = false;

    public PumpkinClicker() {
        // Automatically scale the window size based on your SCALE factor
        setPreferredSize(new Dimension((int)(600 * SCALE), (int)(600 * SCALE)));
        addMouseListener(this);

        // Load images using the class loader path
        background1 = loadImage("farm.jpg");
        background2 = loadImage("nightSky.jpg");
        background3 = loadImage("fullMoon.jpg");
        pumpkinImage = loadImage("pumpkin.png");
        upgradeBackground = loadImage("upgradeBackground.jpg");

        timer = new Timer(1000, e -> step());
        timer.start();
    
        // Register Spacebar Key Binding (press)
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false),
            "pressSpace"
        );

        getActionMap().put("pressSpace", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!spacePressed) {
                    spacePressed = true;
                    triggerPumpkinClick();
                }
            }
        });

        // Register Spacebar Key Binding (release)
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
            KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true),
            "releaseSpace"
        );

        getActionMap().put("releaseSpace", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spacePressed = false;
            }
        });
        
        updateAvailableUpgrades();
    }

    private void triggerPumpkinClick() {
        if (gameOver) return;
        if (upgrades) return;

        if (asc2 && !asc1) pumpkins += pumpkinsPerClick * 3;
        else if (asc1) pumpkins += pumpkinsPerClick * 2;
        else pumpkins += pumpkinsPerClick;

        repaint();
    }

    private Image loadImage(String filename) {
        try {
            URL imgUrl = getClass().getResource("/" + filename);
            if (imgUrl == null) {
                imgUrl = getClass().getResource(filename);
            }

            if (imgUrl != null) {
                return new ImageIcon(imgUrl).getImage();
            } else {
                return new ImageIcon(filename).getImage();
            }
        } catch (Exception e) {
            System.out.println("Could not load image: " + filename);
            return null;
        }
    }

    private void step() {
        if (gameOver) return;

        if (asc2 && !asc1) pumpkins += pumpkinsPerSecond * 3;
        else if (asc1) pumpkins += pumpkinsPerSecond * 2;
        else pumpkins += pumpkinsPerSecond;

        updateAvailableUpgrades();
        repaint();
    }

    private void updateAvailableUpgrades() {
        availableUpgrades.clear();
        if (clickers >= 5 && !click1Bought) {
            availableUpgrades.add("Clicker1");
        }
        if (farms >= 5 && !farm1Bought) {
            availableUpgrades.add("Farm1");
        }
        // Future upgrades can easily be added here!
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Apply global scaling to everything drawn
        g2.scale(SCALE, SCALE);

        int w = (int)(getWidth() / SCALE);
        int h = (int)(getHeight() / SCALE);
        
        if (upgrades) {
            // Upgrades Background
            drawBackground(g2, upgradeBackground, w, h);
            
            // Upgrades HUD
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, h - 80, w, 80);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 18));
            g2.drawString("Pumpkins: " + Convert(pumpkins), 20, h - 50);

            g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
            g2.drawString("Pumpkins Per Second: " + Convert(getShownPPS()), 20, h - 30);
            g2.drawString("Pumpkins Per Click: " + Convert(getShownPPC()), 20, h - 10);

            // Back to Main Button
            g2.setColor(Color.blue);
            g2.fillRect(200, 450, 200, 50);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font ("SansSerif", Font.BOLD, 30));
            g2.drawString("Back", 260, 485);

            // Draw Upgrade Backlog Items
            if (availableUpgrades.isEmpty()) {
                g2.setFont(new Font ("SansSerif", Font.BOLD, 40));
                g2.drawString("You have no available", 100, 200);
                g2.drawString("upgrades currently!", 100, 250);
            } else {
                int startX = 40;
                int startY = 50;
                int boxWidth = 250;
                
                for (int i = 0; i < availableUpgrades.size() && i < 6; i++) {
                    String upgrade = availableUpgrades.get(i);
                    if (upgrade.equals("Clicker1")) {
                        drawUpgrades(g2, "Clicky Clickers:", "Clicker Production x2", "100 Pumpkins", startX, startY);
                    } else if (upgrade.equals("Farm1")) {
                        drawUpgrades(g2, "Fantastic Farms:", "Farm Production x2", "300 Pumpkins", startX, startY);
                    }
                    startX += boxWidth + 20;
                }
            }
        } 
        else {
            // Main Background changes with ascension
            if (asc2) drawBackground(g2, background3, w, h);
            else if (asc1) drawBackground(g2, background2, w, h);
            else drawBackground(g2, background1, w, h);

            // Pumpkin
            int pumpkinW = 150;
            int pumpkinH = 150;
            int pumpkinX = w / 2 - pumpkinW / 2;
            int pumpkinY = h / 2 - pumpkinH / 2;

            if (pumpkinImage != null) {
                g2.drawImage(pumpkinImage, pumpkinX, pumpkinY, pumpkinW, pumpkinH, this);
            } else {
                g2.setColor(Color.ORANGE);
                g2.fillOval(pumpkinX, pumpkinY, pumpkinW, pumpkinH);
            }

            // Main HUD
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, h - 80, w, 80);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 18));
            g2.drawString("Pumpkins: " + Convert(pumpkins), 20, h - 50);

            g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
            g2.drawString("Pumpkins Per Second: " + Convert(getShownPPS()), 20, h - 30);
            g2.drawString("Pumpkins Per Click: " + Convert(getShownPPC()), 20, h - 10);

            // Ascension button
            g2.setColor(Color.RED);
            g2.fillRect(w / 2 - 140, 50, 280, 70);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 16));
            g2.drawString("Bake Pumpkin Pie!", w / 2 - 70, 70);
            g2.setFont(new Font("SansSerif", Font.BOLD, 10));
            g2.drawString("5,000,000 (5 million) pumpkins required to ascend!", w / 2 - 120, 90);
            g2.drawString("Ascension doubles your PPS and PPC!", w / 2 - 100, 110);

            // Upgrades Button
            g2.setColor(Color.blue);
            g2.fillRect(450, 50, 140, 50);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font ("SansSerif", Font.BOLD, 20));
            g2.drawString("Upgrades", 475, 80);

            // Buildings (left)
            drawButton(g2, "", 20, 140);
            g2.drawString("Clicker: " + Convert(clickerCost), 30, 160);
            g2.drawString("+ " + clickerAmount + " PPC", 30, 180);
            drawButton(g2, "", 20, 200);
            g2.drawString("Farm: " + Convert(farmerCost), 30, 220);
            g2.drawString("+ " + farmAmount + " PPS", 30, 240);
            drawButton(g2, "", 20, 260);
            g2.drawString("Patch: " + Convert(patchCost), 30, 280);
            g2.drawString("+ " + patchAmount + " PPS", 30, 300);
            drawButton(g2, "", 20, 320);
            g2.drawString("Business: " + Convert(businessCost), 30, 340);
            g2.drawString("+ " + businessAmount + " PPS", 30, 360);
            drawButton(g2, "", 20, 380);
            g2.drawString("Haunted: " + Convert(hauntedCost), 30, 400);
            g2.drawString("+ " + hauntedAmount + " PPS", 30, 420);

            // Buildings (right)
            drawButton(g2, "", w - 160, 140);
            g2.drawString("Altar: " + Convert(altarCost), 450, 160);
            g2.drawString("+ " + altarAmount + " PPS", 450, 180);
            drawButton(g2, "", w - 160, 200);
            g2.drawString("Temple: " + Convert(templeCost), 450, 220);
            g2.drawString("+ " + templeAmount + " PPS", 450, 240);
            drawButton(g2, "", w - 160, 260);
            g2.drawString("Demon: " + Convert(demonCost), 450, 280);
            g2.drawString("+ " + demonAmount + " PPS", 450, 300);
            drawButton(g2, "", w - 160, 320);
            g2.drawString("Overlord: " + Convert(overlordCost), 450, 340);
            g2.drawString("+ " + overlordAmount + " PPS", 450, 360);
            drawButton(g2, "", w - 160, 380);
            g2.drawString("GOD: " + Convert(godCost), 450, 400);
            g2.drawString("+ " + godAmount + " PPS", 450, 420);

            // Building Quantities
            drawAmount(g2, "" + clickers, 140, 140);
            drawAmount(g2, "" + farms, 140, 200);
            drawAmount(g2, "" + patches, 140, 260);
            drawAmount(g2, "" + businesses, 140, 320);
            drawAmount(g2, "" + haunteds, 140, 380);

            drawAmount(g2, "" + altars, 560, 140);
            drawAmount(g2, "" + temples, 560, 200);
            drawAmount(g2, "" + demons, 560, 260);
            drawAmount(g2, "" + overlords, 560, 320);
            drawAmount(g2, "" + gods, 560, 380);

            if (gameOver) {
                g2.setColor(new Color(0, 255, 0, 220));
                g2.fillRect(0, 0, w, h);

                g2.setColor(Color.BLACK);
                g2.setFont(new Font("SansSerif", Font.BOLD, 28));
                g2.drawString("Congratulations!", w / 2 - 110, h / 2 - 40);

                g2.setFont(new Font("SansSerif", Font.PLAIN, 18));
                g2.drawString("Thank you for playing Pumpkin Clicker!", w / 2 - 170, h / 2);
                g2.drawString("Be sure to vote for our awesome game!", w / 2 - 170, h / 2 + 40);
            }
        }
    }

    private void drawBackground(Graphics2D g2, Image img, int w, int h) {
        if (img != null) {
            g2.drawImage(img, 0, 0, w, h, this);
        } else {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, w, h);
        }
    }

    private void drawButton(Graphics2D g2, String text, int x, int y) {
        g2.setColor(Color.GREEN.darker());
        g2.fillRect(x, y, 120, 50);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g2.drawString(text, x + 10, y + 30);
    }

    private void drawAmount(Graphics2D g2, String text, int x, int y) {
        g2.setColor(Color.BLACK.darker());
        g2.fillRect(x, y, 30, 50);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g2.drawString(text, x + 10, y + 30);
    }

    private void drawUpgrades(Graphics2D g2, String title, String effect, String cost, int x, int y) {
        g2.setColor(Color.GRAY);
        g2.fillRect(x, y, 250, 100);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 22));
        g2.drawString(title, x + 10, y + 25);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
        g2.drawString(effect, x + 10, y + 55);
        g2.drawString(cost, x + 10, y + 85);
    }

    private long getShownPPS() {
        if (asc1) return pumpkinsPerSecond * 2;
        if (asc2 && !asc1) return pumpkinsPerSecond * 3;
        return pumpkinsPerSecond;
    }

    private long getShownPPC() {
        if (asc1) return pumpkinsPerClick * 2;
        if (asc2 && !asc1) return pumpkinsPerClick * 3;
        return pumpkinsPerClick;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x = (int)(e.getX() / SCALE);
        int y = (int)(e.getY() / SCALE);
        int w = (int)(getWidth() / SCALE);

        if (gameOver) return;

        int pumpkinW = 150;
        int pumpkinH = 150;
        int pumpkinX = w / 2 - pumpkinW / 2;
        int pumpkinY = (int)(getHeight() / SCALE) / 2 - pumpkinH / 2;

        // Clicking pumpkin with mouse
        if (x >= pumpkinX && x <= pumpkinX + pumpkinW && y >= pumpkinY && y <= pumpkinY + pumpkinH) {
            triggerPumpkinClick();
        }

        // Handle Main Screen vs Upgrades Screen Clicks
        if (!upgrades) {
            handleBuildings(x, y, w);
            
            // Open Upgrades button
            if (x >= 450 && x <= 590 && y >= 50 && y <= 100) {
                upgrades = true;
                timer.stop();
                repaint();
            }
        } 
        else {
            handleUpgrades(x, y);

            // Return to Main button
            if (x >= 200 && x <= 400 && y >= 450 && y <= 500) {
                upgrades = false;
                timer.start();
                repaint();
            }
        }

        // Ascension 1
        if (x >= w / 2 - 90 && x <= w / 2 + 90 && y >= 60 && y <= 110 && pumpkins >= req1 && !asc1) {
            pumpkins = 0;
            asc1 = true;
            pumpkinsPerSecond = 0;
            pumpkinsPerClick = 1;
            resetCosts();
        }

        // Ascension 2
        if (x >= w / 2 - 90 && x <= w / 2 + 90 && y >= 60 && y <= 110 && pumpkins >= req2 && asc1 && !asc2) {
            pumpkins = 0;
            asc2 = true;
            pumpkinsPerSecond = 0;
            pumpkinsPerClick = 1;
            resetCosts();
        }

        // Final ascension
        if (x >= w / 2 - 90 && x <= w / 2 + 90 && y >= 60 && y <= 110 && pumpkins >= req3 && asc2) {
            gameOver = true;
        }

        repaint();
    }

    private void resetCosts() {
        clickerCost = 15;
        farmerCost = 50;
        patchCost = 100;
        businessCost = 250;
        hauntedCost = 750;
        altarCost = 1000;
        templeCost = 15000;
        demonCost = 50000;
        overlordCost = 100000;
        godCost = 500000;
        clickers = 0;
        farms = 0;
        patches = 0;
        businesses = 0;
        haunteds = 0;
        altars = 0;
        temples = 0;
        demons = 0;
        overlords = 0;
        gods = 0;
    }

    private void handleBuildings(int x, int y, int w) {
        // Left side buildings
        if (x >= 20 && x <= 140 && y >= 140 && y <= 190 && pumpkins >= clickerCost) {
            pumpkinsPerClick += clickerAmount;
            pumpkins -= clickerCost;
            clickers++;
            clickerCost = (long) (15 * (Math.pow(1.3, clickers)));
        } else if (x >= 20 && x <= 140 && y >= 200 && y <= 250 && pumpkins >= farmerCost) {
            pumpkinsPerSecond += farmAmount;
            pumpkins -= farmerCost;
            farms++;
            farmerCost = (long) (60 * (Math.pow(1.3, farms)));
        } else if (x >= 20 && x <= 140 && y >= 260 && y <= 310 && pumpkins >= patchCost) {
            pumpkinsPerSecond += patchAmount;
            pumpkins -= patchCost;
            patches++;
            patchCost = (long) (350 * (Math.pow(1.3, patches)));
        } else if (x >= 20 && x <= 140 && y >= 320 && y <= 370 && pumpkins >= businessCost) {
            pumpkinsPerSecond += businessAmount;
            pumpkins -= businessCost;
            businesses++;
            businessCost = (long) (1500 * (Math.pow(1.3, businesses)));
        } else if (x >= 20 && x <= 140 && y >= 380 && y <= 430 && pumpkins >= hauntedCost) {
            pumpkinsPerSecond += hauntedAmount;
            pumpkins -= hauntedCost;
            haunteds++;
            hauntedCost = (long) (7500 * (Math.pow(1.3, haunteds)));
        }

        // Right side building
        int rx = w - 140;
        if (x >= rx && x <= rx + 120 && y >= 140 && y <= 190 && pumpkins >= altarCost) {
            pumpkinsPerSecond += altarAmount;
            pumpkins -= altarCost;
            altars++;
            altarCost = (long) (45000 * (Math.pow(1.3, altars)));
        } else if (x >= rx && x <= rx + 120 && y >= 200 && y <= 250 && pumpkins >= templeCost) {
            pumpkinsPerSecond += templeAmount;
            pumpkins -= templeCost;
            temples++;
            templeCost = (long) (300000 * (Math.pow(1.3, temples)));
        } else if (x >= rx && x <= rx + 120 && y >= 260 && y <= 310 && pumpkins >= demonCost) {
            pumpkinsPerSecond += demonAmount;
            pumpkins -= demonCost;
            demons++;
            demonCost = (long) (2500000 * (Math.pow(1.3, demons)));
        } else if (x >= rx && x <= rx + 120 && y >= 320 && y <= 370 && pumpkins >= overlordCost) {
            pumpkinsPerSecond += overlordAmount;
            pumpkins -= overlordCost;
            overlords++;
            overlordCost = (long) (20000000 * (Math.pow(1.3, overlords)));
        } else if (x >= rx && x <= rx + 120 && y >= 380 && y <= 430 && pumpkins >= godCost) {
            pumpkinsPerSecond += godAmount;
            pumpkins -= godCost;
            gods++;
            godCost = (long) (150000000 * (Math.pow(1.3, gods)));
        }
        
        updateAvailableUpgrades();
    }
    
    public void handleUpgrades(int x, int y) {
        int startX = 30;
        int startY = 50;
        int boxWidth = 250;
        int boxHeight = 100;

        for (int i = 0; i < availableUpgrades.size() && i < 6; i++) {
            if (x >= startX && x <= startX + boxWidth && y >= startY && y <= startY + boxHeight) {
                String upgrade = availableUpgrades.get(i);
                
                if (upgrade.equals("Clicker1") && pumpkins >= 100) {
                    pumpkins -= 100;
                    clickerAmount *= 2;
                    pumpkinsPerClick = 1 + (clickers * clickerAmount);
                    click1Bought = true;
                    availableUpgrades.remove(i);
                    System.out.println("Bought Clicker1!");
                    break;
                } else if (upgrade.equals("Farm1") && pumpkins >= 300) {
                    pumpkins -= 300;
                    farmAmount *= 2;
                    pumpkinsPerSecond = (farms * farmAmount) + (patches * patchAmount) + (businesses * businessAmount) + (haunteds * hauntedAmount) + (altars * altarAmount) + (temples * templeAmount) + (demons * demonAmount) + (overlords * overlordAmount) + (gods * godAmount);
                    farm1Bought = true;
                    availableUpgrades.remove(i);
                    System.out.println("Bought Farm1!");
                    break;
                }
            }
            startX += boxWidth + 20;
        }
    }

    public static String Convert(long value) {
        String result = String.valueOf(value);
        
        if (value >= 1000000000) {
            double placeVal = (double) value / 1000000000;
            String ppsString = String.valueOf(placeVal);
            int period = ppsString.indexOf(".");
            if (period + 4 <= ppsString.length()) {
                result = ppsString.substring(0, period + 4) + "bil";
            } else {
                result = ppsString + "bil";
            }
        } 
        else if (value >= 1000000) {
            double placeVal = (double) value / 1000000;
            String ppsString = String.valueOf(placeVal);
            int period = ppsString.indexOf(".");
            if (period + 4 <= ppsString.length()) {
                result = ppsString.substring(0, period + 4) + "mil";
            } else {
                result = ppsString + "mil";
            }
        } 
        else if (value >= 1000) {
            double placeVal = (double) value / 1000;
            String ppsString = String.valueOf(placeVal);
            int period = ppsString.indexOf(".");
            if (period + 4 <= ppsString.length()) {
                result = ppsString.substring(0, period + 4) + "k";
            } else {
                result = ppsString + "k";
            }
        }
        return result;
    }

    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pumpkin Clicker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        PumpkinClicker game = new PumpkinClicker();
        frame.add(game);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}