import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.*;

public class RubiksAverage {
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

  RubiksAverage(String path, int cubes) {
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
            g.setColor(averageClosest(j, i));
            g.fillRect(j * 3, i * 3, 3, 3);
            g.setColor(Color.black);
            g.drawRect(j * 3, i * 3, 3, 3);
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

  Color averageClosest(int x, int y) {
    double tBlue = 0;
    double tGreen = 0;
    double tRed = 0;
    for (int i = x * sqLength + xSpace; i < (x + 1) * sqLength + xSpace; i++) {
      for (int j = y * sqLength + ySpace; j < (y + 1) * sqLength + ySpace; j++) {
        int rgb = image.getRGB(i, j);
        tBlue += rgb & 0xff;
        tGreen += (rgb & 0xff00) >> 8;
        tRed += (rgb & 0xff0000) >> 16;
      }
    }
    tBlue /= sqLength * sqLength;
    tGreen /= sqLength * sqLength;
    tRed /= sqLength * sqLength;
    double eBlue = tBlue;
    double eGreen = tGreen;
    double eRed = tRed;
    if (x != 0) {
      eBlue += error.get(y).get(x - 1).get(2) * (7.0/16);
      eGreen += error.get(y).get(x - 1).get(1) * (7.0/16);
      eRed += error.get(y).get(x - 1).get(0) * (7.0/16);
    }
    if (y != 0) {
      eBlue += error.get(y - 1).get(x).get(2) * (5.0/16);
      eGreen += error.get(y - 1).get(x).get(1) * (5.0/16);
      eRed += error.get(y - 1).get(x).get(0) * (5.0/16);
      if (x != cWidth * 3 - 1) {
        eBlue += error.get(y - 1).get(x + 1).get(2) * (3.0/16);
        eGreen += error.get(y - 1).get(x + 1).get(1) * (3.0/16);
        eRed += error.get(y - 1).get(x + 1).get(0) * (3.0/16);
      }
      if (x != 0) {
        eBlue += error.get(y - 1).get(x - 1).get(2) * (1.0/16);
        eGreen += error.get(y - 1).get(x - 1).get(1) * (1.0/16);
        eRed += error.get(y - 1).get(x - 1).get(0) * (1.0/16);
      }
    }
    double dif = Double.POSITIVE_INFINITY;
    Color closestC = Color.white;
    double rError = 0;
    double gError = 0;
    double bError = 0;
    for (Color c : rColors) {
      double dist = Math.sqrt(Math.pow(eRed - c.getRed(), 2) + Math.pow(eGreen - c.getGreen(), 2)
              + Math.pow(eBlue - c.getBlue(), 2));
      if (dist < dif) {
        dif = dist;
        closestC = c;
        rError = tRed - c.getRed();
        gError = tGreen - c.getGreen();
        bError = tBlue - c.getBlue();
      }
    }
    error.get(y).get(x).addAll(Arrays.asList(rError, gError, bError));
    return closestC;
  }
}