package Visualize;

import Algorithm_Ops.Circle;
import Dataset.Events;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author Cay Horstmann
 * @version 1.32 2007-04-14
 */
public class Visualize {
    public static ArrayList<Events> points;
    public static ArrayList<Circle> circles;

    public void drawCircles(ArrayList<Events> events, ArrayList<Circle> circles1) {
        EventQueue.invokeLater(() -> {
            DrawFrame frame = new DrawFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            points = events;
            circles = circles1;
        });
    }
}

/**
 * A frame that contains a panel with drawings
 */
class DrawFrame extends JFrame {
    public DrawFrame() {
        setTitle("Title");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
//        System.out.println(width + " " + height);
//        pack();
//        setSize((int) width, (int) height);
        setSize((int) width, (int) height);
        // add panel to frame
        add(new JPanel(), BorderLayout.NORTH);
        add(new JScrollPane(), BorderLayout.CENTER);
        DrawComponent component = new DrawComponent();
        add(component);
    }

//    public static final int DEFAULT_WIDTH = 5000;
//
//    public static final int DEFAULT_HEIGHT = 5000;
}

/**
 * A component that displays rectangles and ellipses.
 */
class DrawComponent extends JComponent {
    double min_x = 1000000;
    double min_y = 1000000;
    double max_x = -1000000;
    double max_y = -1000000;
    double leftX = 0;
    double topY = 0;
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    double width = screenSize.getWidth();
    double height = screenSize.getHeight();
    //    double width = 1900;
//    double height = 1040;
    double scale = 5;

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

//        g2.translate(width/2, height/2);
//        g2.scale(scale, scale);
//        g2.translate(-width/2, -height/2);

        ArrayList<Events> new_points = new ArrayList<>();
        drawRectangle(g2);
        drawPoints(g2, new_points);
        drawCircles(g2);
    }

    private void drawRectangle(Graphics2D g2) {
        Rectangle2D rect = new Rectangle2D.Double(leftX, topY, width, height);
        g2.draw(rect);
    }

    private void drawCircles(Graphics2D g2) {
        double average_x = 0;
        double average_y = 0;

        int total = Visualize.circles.size();
        total -= 4;


        for (Circle circle : Visualize.circles) {
            double x = circle.getX_coord();
            double y = circle.getY_coord();
            double r = circle.getRadius();
//            System.out.println(r + " " + x + " " + y);
//            System.out.print(circle.toString());
            average_x += x;
            average_y += y;
            if (x - r < min_x)
                min_x = x - r;
            if (x + r > max_x)
                max_x = x + r;
            if (y - r < min_y)
                min_y = y - r;
            if (y + r > max_y)
                max_y = y + r;
//            g2.fill();(circle);
        }
        average_x /= total;
        average_y /= total;


        boolean first = true;
        String color = "G";         //pattern is Y GBK GBK GBK...
        double prev_x = 0, prev_y = 0;
        /*
        * Start circle-red
        * growth is magenta
        * shift is green
        * shift2 is blue
        * shift3 is yellow
        * points are black
        * */
        for (Circle circle : Visualize.circles) {
            double x = circle.getX_coord();
            double y = circle.getY_coord();
            double r = circle.getRadius();

            Circle scaled_circle = new Circle();
            //there will be two radius after scaling, since uneven scaling
            double radius_horizontal = width * (r) / (max_x - min_x);
            double radius_vertical = height * (r) / (max_y - min_y);
            scaled_circle.setX_coord(width * (x - min_x) / (max_x - min_x));
            scaled_circle.setY_coord(height * (y - min_y) / (max_y - min_y));

            Ellipse2D.Double scaled_ellipse = new Ellipse2D.Double(scaled_circle.getX_coord() - radius_horizontal, scaled_circle.getY_coord() - radius_vertical, 2 * radius_horizontal, 2 * radius_vertical);
            if (first) {
                g2.setColor(Color.black);
                first = false;
                g2.draw(scaled_ellipse);
                g2.setColor(Color.magenta);
                prev_x = x;
                prev_y = y;
                continue;
            }
            if (prev_x != x || prev_y != y) {
                if (color.equals("G")) {
                    g2.setColor(Color.GREEN);
                    color = "B";

                } else if (color.equals("B")) {
                    g2.setColor(Color.BLUE);
                    color = "Y";

                } else if (color.equals("Y")) {
                    g2.setColor(Color.YELLOW);
                    color = "G";

                }
                g2.draw(scaled_ellipse);
                g2.setColor(Color.magenta);
                prev_x = x;
                prev_y = y;
                continue;
            }
            prev_x = x;
            prev_y = y;
            g2.draw(scaled_ellipse);
        }
    }

    private void drawPoints(Graphics2D g2, ArrayList<Events> new_points) {
//        System.out.println();
        double lat, lon;
        for (Events e : Visualize.points) {

            Events event = new Events();
            lat = e.getLat();
            lon = e.getLon();
            if (lat <= max_y && lat >= min_y && lon <= max_x && lon >= min_x) {
                event.setLon(width * (e.getLon() - min_x) / (max_x - min_x));
                event.setLat(height * (e.getLat() - min_y) / (max_y - min_y));
                new_points.add(event);
            }
        }
        Ellipse2D.Double ellipse;
        g2.setColor(Color.black);
        int point_size = 3;

        for (int i = 0; i < new_points.size(); i++) {
            Events e = new_points.get(i);
            double alp = checkpos(e, i, new_points);
            double alp2 = checkpos(e, i, new_points);
            if (alp != 0) {
                g2.setColor(Color.RED);
                point_size = 1;
            } else {
                g2.setColor(Color.BLACK);
                point_size = 3;
            }
            Random r = new Random();
            int sign = r.nextInt(4);
            ellipse = null;
            if (sign == 0) {
                ellipse = new Ellipse2D.Double(e.getLon() - (point_size / 2) + alp, e.getLat() - (point_size / 2) + alp2, point_size, point_size);
            }
            if (sign == 1) {
                ellipse = new Ellipse2D.Double(e.getLon() - (point_size / 2) + alp, e.getLat() - (point_size / 2) - alp2, point_size, point_size);
            }
            if (sign == 2) {
                ellipse = new Ellipse2D.Double(e.getLon() - (point_size / 2) - alp, e.getLat() - (point_size / 2) + alp2, point_size, point_size);
            }
            if (sign == 3) {
                ellipse = new Ellipse2D.Double(e.getLon() - (point_size / 2) - alp, e.getLat() - (point_size / 2) - alp2, point_size, point_size);
            }
            g2.fill(ellipse);
        }
    }

    private double checkpos(Events e, int i, ArrayList<Events> new_points) {
        for (int j = 0; j < i; j++) {
            if (e.getLat() == new_points.get(j).getLat() && e.getLon() == new_points.get(j).getLon()) {

                Random r = new Random();
                double a = r.nextDouble();
                a = 10 * a;
                return a;
            }
        }
        return 0;

    }
}
