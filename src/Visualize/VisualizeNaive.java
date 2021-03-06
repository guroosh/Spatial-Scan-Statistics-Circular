package Visualize;

import Algorithm_Ops.Circle;
import Dataset.Events;
import TestingAlgo.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author Cay Horstmann
 * @version 1.32 2007-04-14
 */
public class VisualizeNaive {
    public static ArrayList<Events> points;
    public static ArrayList<Circle> circles;
    public static String title;

    public void drawCircles(ArrayList<Events> events, ArrayList<Circle> circles1, String title1) {
        EventQueue.invokeLater(() -> {
            DrawFrameNaive frame = new DrawFrameNaive();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            points = events;
            circles = circles1;
            title = title1;
        });
    }
}

/**
 * A frame that contains a panel with drawings
 */
class DrawFrameNaive extends JFrame {
    public DrawFrameNaive() {
        setTitle(VisualizeNaive.title);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        setSize((int) width, (int) height);
        add(new JPanel(), BorderLayout.NORTH);
        add(new JScrollPane(), BorderLayout.CENTER);
        DrawComponentNaive component = new DrawComponentNaive();
        add(component);
    }
}

/**
 * A component that displays rectangles and ellipses.
 */
class DrawComponentNaive extends JComponent {
    double min_x = 1000000;
    double min_y = 1000000;
    double max_x = -1000000;
    double max_y = -1000000;
    double leftX = 0;
    double topY = 0;
    //    double width = 1300;
//    double height = 680;
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    double width = screenSize.getWidth();
    double height = screenSize.getHeight();

    Color background = Color.WHITE;
    Color grids = Color.WHITE;
    Color points_main = Color.BLACK;
    Color points_scattered = Color.RED;
    Color naive_circles = Color.GREEN;
    Color moving_circles = Color.BLUE;

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        ArrayList<Events> new_points = new ArrayList<>();
        g2.setColor(background);
        drawBackground(g2);
        g2.setColor(grids);
        drawGrids(g2);
        drawPoints(g2, new_points);
        g2.setColor(naive_circles);
        g2.setStroke(new BasicStroke(2));
        drawCircles(g2);
    }

    private void drawGrids(Graphics2D g2) {
        for (Circle circle : VisualizeNaive.circles) {
            if (circle.getRadius() == -7) {
                continue;
            }
            double x = circle.getX_coord();
            double y = circle.getY_coord();
            double r = circle.getRadius();
            if (x - r < min_x)
                min_x = x - r;
            if (x + r > max_x)
                max_x = x + r;
            if (y - r < min_y)
                min_y = y - r;
            if (y + r > max_y)
                max_y = y + r;
        }
        double x1, x2, y1, y2;
        x1 = leftX;
        x2 = width;
        for (double d : Main.gridFile_global.yScale) {
            y1 = height * (d - min_y) / (max_y - min_y);
            y2 = y1;
            g2.draw(new Line2D.Double(x1, y1, x2, y2));
        }
        y1 = topY;
        y2 = height;
        for (double d : Main.gridFile_global.xScale) {
            x1 = width * (d - min_x) / (max_x - min_x);
            x2 = x1;
            g2.draw(new Line2D.Double(x1, y1, x2, y2));
        }
    }

    private void drawBackground(Graphics2D g2) {
        Rectangle2D rect = new Rectangle2D.Double(leftX, topY, width, height);
        g2.fill(rect);
    }

    private void drawCircles(Graphics2D g2) {
        double average_x = 0;
        double average_y = 0;


        int total = VisualizeNaive.circles.size();
        total -= 4;
//        System.out.println("Total circles drawn: " + total);

//        System.out.println(VisualizeNaive.circles.size());

        //uncomment if problem
//        for (Circle circle : VisualizeNaive.circles) {
//            if (circle.getRadius() == -7) {
//                continue;
//            }
//            double x = circle.getX_coord();
//            double y = circle.getY_coord();
//            double r = circle.getRadius();
//            average_x += x;
//            average_y += y;
//            if (x - r < min_x)
//                min_x = x - r;
//            if (x + r > max_x)
//                max_x = x + r;
//            if (y - r < min_y)
//                min_y = y - r;
//            if (y + r > max_y)
//                max_y = y + r;
//        }

        average_x /= total;
        average_y /= total;
//        System.out.println("Average: " + average_x + ", " + average_y);
//        System.out.println("Range: " + min_x + " to " + max_x + " and " + min_y + " to " + max_y);
        int i = 0;
        for (Circle circle : VisualizeNaive.circles) {
            if (circle.getRadius() == -7) {
                g2.setColor(moving_circles);
                continue;
            }
            i++;
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
            g2.draw(scaled_ellipse);
        }
    }

    private void drawPoints(Graphics2D g2, ArrayList<Events> new_points) {
        double lat, lon;
        for (Events e : VisualizeNaive.points) {
//            if (e.marked)
//                continue;
            Events event = new Events();
            lat = e.getY();
            lon = e.getX();
//            System.out.println(e.toString());
            if (lat <= max_y && lat >= min_y && lon <= max_x && lon >= min_x) {
//                System.out.println("inside");
                event.setX(width * (e.getX() - min_x) / (max_x - min_x));
                event.setY(height * (e.getY() - min_y) / (max_y - min_y));
                new_points.add(event);
            }
        }
//        System.out.println(max_x);
//        System.out.println(max_y);
//        System.out.println(min_x);
//        System.out.println(min_y);
        Ellipse2D.Double ellipse = new Ellipse2D.Double();
        int point_size;

        for (int i = 0; i < new_points.size(); i++) {
            Events e = new_points.get(i);
            double alp = checkpos(e, i, new_points);
            double alp2 = checkpos(e, i, new_points);
            if (alp != 0) {
                g2.setColor(points_scattered);
                point_size = 1;
            } else {
                g2.setColor(points_main);
                point_size = 3;
            }
            Random r = new Random();
            int sign = r.nextInt(4);
            if (sign == 0) {
                ellipse = new Ellipse2D.Double(e.getX() - (point_size / 2) + alp, e.getY() - (point_size / 2) + alp2, point_size, point_size);
            }
            if (sign == 1) {
                ellipse = new Ellipse2D.Double(e.getX() - (point_size / 2) + alp, e.getY() - (point_size / 2) - alp2, point_size, point_size);
            }
            if (sign == 2) {
                ellipse = new Ellipse2D.Double(e.getX() - (point_size / 2) - alp, e.getY() - (point_size / 2) + alp2, point_size, point_size);
            }
            if (sign == 3) {
                ellipse = new Ellipse2D.Double(e.getX() - (point_size / 2) - alp, e.getY() - (point_size / 2) - alp2, point_size, point_size);
            }
            g2.fill(ellipse);
        }
    }

    private double checkpos(Events e, int i, ArrayList<Events> new_points) {
        for (int j = 0; j < i; j++) {
            if (e.getY() == new_points.get(j).getY() && e.getX() == new_points.get(j).getX()) {

                Random r = new Random();
                double a = r.nextDouble();
                a = 10 * a;
                return a;
            }
        }
        return 0;

    }
}