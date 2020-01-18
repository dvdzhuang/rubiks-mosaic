import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.*;

public class RubiksImage {
  String path;
  int cubes;
  int cWidth;
  int cHeight;
  BufferedImage image;
  int sqLength;
  int xSpace;
  int ySpace;
  ArrayList<ArrayList<ArrayList<Double>>> error;
  final Color[] rColors = {new Color(0, 155, 72), new Color(183, 18, 52),
          new Color(0, 70, 173), new Color(255, 88, 0),
          new Color(255, 213, 0), new Color(255, 255, 255)};

  RubiksImage(String path, int cubes) {
    this.path = path;
    this.cubes = cubes;
    try {
      this.image = ImageIO.read(new File(path));
    } catch (IOException e) {
      throw new IllegalArgumentException("File invalid");
    }
    cWidth = bestRatioWidth(this.cubes, image.getWidth(), image.getHeight());
    cHeight = cubes / cWidth;
    System.out.println(cWidth + " " + cHeight);
    sqLength = Math.min(image.getWidth() / (3 * cWidth), image.getHeight() / (3 * cHeight));
    System.out.println(sqLength);

    this.error = new ArrayList<>();
    JFrame frame = new JFrame();
    frame.setSize(new Dimension(image.getWidth(), image.getHeight()));
    xSpace = (image.getWidth() - (sqLength * cWidth * 3)) / 2;
    ySpace = (image.getHeight() - (sqLength * cHeight * 3)) / 2;
    JComponent jc = new JComponent() {
      public void paint(Graphics g) {
        for (int i = 0; i < cHeight * 3; i++) {
          error.add(new ArrayList<ArrayList<Double>>());
          for (int j = 0; j < cWidth * 3; j++) {
            error.get(i).add(new ArrayList<>());
            g.setColor(squareClosest(j, i));
            g.fillRect(j * sqLength, i * sqLength, sqLength, sqLength);
            g.setColor(Color.black);
            g.drawRect(j * sqLength, i * sqLength, sqLength, sqLength);
          }
        }
      }
    };
    frame.add(jc);
    frame.setVisible(true);
  }

  int bestRatioWidth(int cubes, int tWidth, int tHeight) {
    ArrayList<Double> ratios = new ArrayList<Double>();
    ArrayList<Integer> widths = new ArrayList<>();
    for (int i = 1; i <= Math.sqrt(cubes); i++) {
      if (cubes % i == 0) {
        ratios.add(((double) i) / (cubes / i));
        ratios.add(((double) (cubes / i)) / i);
        widths.add(i);
        widths.add(cubes / i);
      }
    }
    double tempRatio = ((double) tWidth) / tHeight;
    double dif = Double.POSITIVE_INFINITY;
    int pos = 0;
    for (int i = 0; i < ratios.size(); i++) {
      if (Math.abs(ratios.get(i) - tempRatio) < dif) {
        dif = Math.abs(ratios.get(i) - tempRatio);
        pos = i;
      }
    }
    return widths.get(pos);
  }

  Color closestColor(int rgb) {
    int blue = rgb & 0xff;
    int green = (rgb & 0xff00) >> 8;
    int red = (rgb & 0xff0000) >> 16;
    double dif = Double.POSITIVE_INFINITY;
    Color closestC = Color.white;
    for (Color c : rColors) {
      double dist = Math.sqrt(Math.pow(red - c.getRed(), 2) + Math.pow(green - c.getGreen(), 2)
              + Math.pow(blue - c.getBlue(), 2));
      if (dist < dif) {
        dif = dist;
        closestC = c;
      }
    }
    return closestC;
  }

  Color squareClosest(int x, int y) {
    double blue = 0;
    double green = 0;
    double red = 0;
    double orange = 0;
    double yellow = 0;
    double white = 0;
    if (x != 0) {
      blue += error.get(y).get(x - 1).get(0) * (7.0/16);
      green += error.get(y).get(x - 1).get(1) * (7.0/16);
      red += error.get(y).get(x - 1).get(2) * (7.0/16);
      orange += error.get(y).get(x - 1).get(3) * (7.0/16);
      yellow += error.get(y).get(x - 1).get(4) * (7.0/16);
      white += error.get(y).get(x - 1).get(5) * (7.0/16);
    }
    if (y != 0) {
      blue += error.get(y - 1).get(x).get(0) * (5.0/16);
      green += error.get(y - 1).get(x).get(1) * (5.0/16);
      red += error.get(y - 1).get(x).get(2) * (5.0/16);
      orange += error.get(y - 1).get(x).get(3) * (5.0/16);
      yellow += error.get(y - 1).get(x).get(4) * (5.0/16);
      white += error.get(y - 1).get(x).get(5) * (5.0/16);
      if (x != cWidth * 3 - 1) {
        blue += error.get(y - 1).get(x + 1).get(0) * (3.0/16);
        green += error.get(y - 1).get(x + 1).get(1) * (3.0/16);
        red += error.get(y - 1).get(x + 1).get(2) * (3.0/16);
        orange += error.get(y - 1).get(x + 1).get(3) * (3.0/16);
        yellow += error.get(y - 1).get(x + 1).get(4) * (3.0/16);
        white += error.get(y - 1).get(x + 1).get(5) * (3.0/16);
      }
      if (x != 0) {
        blue += error.get(y - 1).get(x - 1).get(0) * (1.0/16);
        green += error.get(y - 1).get(x - 1).get(1) * (1.0/16);
        red += error.get(y - 1).get(x - 1).get(2) * (1.0/16);
        orange += error.get(y - 1).get(x - 1).get(3) * (1.0/16);
        yellow += error.get(y - 1).get(x - 1).get(4) * (1.0/16);
        white += error.get(y - 1).get(x - 1).get(5) * (1.0/16);
      }
    }
    for (int i = x * sqLength; i < (x + 1) * sqLength; i++) {
      for (int j = y * sqLength; j < (y + 1) * sqLength; j++) {
        Color closest = closestColor(image.getRGB(i + xSpace, j + ySpace));
        if (closest.equals(rColors[0])) {
          green++;
        } else if (closest.equals(rColors[1])) {
          red++;
        } else if (closest.equals(rColors[2])) {
          blue++;
        } else if (closest.equals(rColors[3])) {
          orange++;
        } else if (closest.equals(rColors[4])) {
          yellow++;
        } else {
          white++;
        }
      }
    }
    double most = Math.max(blue, Math.max(green, Math.max(red, Math.max(orange, Math.max(yellow,
            white)))));
    error.get(y).get(x).add(blue);
    error.get(y).get(x).add(green);
    error.get(y).get(x).add(red);
    error.get(y).get(x).add(orange);
    error.get(y).get(x).add(yellow);
    error.get(y).get(x).add(white);
    if (blue == most) {
      error.get(y).get(x).set(0, 0.0);
      return rColors[2];
    } else if (green == most) {
      error.get(y).get(x).set(1, 0.0);
      return rColors[0];
    } else if (red == most) {
      error.get(y).get(x).set(2, 0.0);
      return rColors[1];
    } else if (orange == most) {
      error.get(y).get(x).set(3, 0.0);
      return rColors[3];
    } else if (yellow == most) {
      error.get(y).get(x).set(4, 0.0);
      return rColors[4];
    } else {
      error.get(y).get(x).set(5, 0.0);
      return rColors[5];
    }
  }
}
