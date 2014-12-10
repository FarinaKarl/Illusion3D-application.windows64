import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Illusion3D extends PApplet {

PImage img, shape; // original image and it's shape
PGraphics osCanvas;
int [][] x; // array containing shape essential points
int r, c, gap; // raws, columns, raws gap
int gapIndex; // it is inversely proportional to the gap
int rangeBlack = 40;
float rawsWeight = 2;
float bending = .6f;
boolean useKeys, changeGap;
int bgColor;
int imageColor;
int linesColor = 0xff000000;

public void setup() {  
  background(30);

  img = loadImage("image.png");
  size(img.width * 2, img.height);

  x = new int [width][height];

  if (!changeGap) // initialize gapIndex
    gapIndex = height / 6;    
  gap = height / gapIndex; // initialize gap

  drawShape();
}

public void draw() {     
  image(img, width / 2, 0);
}

public void drawShape() { // creates the shape
  bgColor = img.get(0, 0);

  osCanvas = createGraphics (img.width, img.height);
  osCanvas.beginDraw();
  osCanvas.background(255);
  for (int j = 1; j < img.height-1; j++) {
    for (int i = 0; i < img.width; i++) {
      if (color(img.get(i, j)) != bgColor && ((color(img.get(i, j-1)) == bgColor) || (color(img.get(i, j+1)) == bgColor))) {
        osCanvas.stroke(0);
        osCanvas.point(i, j);
      }
      if (color(img.get(i, j)) != bgColor && ((color(img.get(i-1, j)) == bgColor) || (color(img.get(i+1, j)) == bgColor))) {
        osCanvas.stroke(0);
        osCanvas.point(i, j);
      }
    }
  }
  osCanvas.endDraw();

  osCanvas.save("shape.png"); // saves shape
  shape = loadImage("shape.png"); // reload it in "shape"
}

public void drawImage() { // draws the image on the left

  if (!changeGap) { // only if the gap is not changing
    imageColor = color(img.get(mouseX - width / 2, mouseY)); // changes image color
    drawLines(gap);
  }

  preparePoints(x, gap);
  drawCurves(x, gap);
}

public void drawLines(int gap) { // draws the background lines
  for (int j = 0; j < height; j+= gap) {
    strokeWeight(rawsWeight);
    stroke(linesColor);
    line(0, j, width / 2, j);
  }
}

public void preparePoints(int [][] x, int gap) { // sets the essential points of the shape

  for (int j = 0; j < height; j+= gap) {
    for (int i = 0; i < width / 2; i++) {
      if (color(shape.get(i, j)) < color(rangeBlack)) { // if this point is black
        x[r][c] = i;
        while (color (shape.get (i, j)) < color(rangeBlack)) // while the pixels are black
            i++;
        r++; // increments the raws
      }
    }
    c++; // increments the columns
  }
  r = 0; // reset raws
  c = 0; // reset columns
}

public void drawCurves(int [][] x, int gap) { // draws curves that give the 3D illusion

  for (int c = 0, y = 0; c < height; c++, y+=gap) {
    for (int r = 0; r < width / 2 -1; r++) {
      if (x[r][c] != 0 && x[r+1][c] != 0) { // if the x values are not null
        strokeWeight(rawsWeight);
        stroke(imageColor);
        if (img.get(((x[r][c]) + x[r+1][c]) / 2, y) != color(bgColor)) // if the point between two black points != background color
            bezier(x[r][c], y, x[r][c] + (x[r+1][c] - x[r][c]) / 2, y - (gap * bending), x[r][c] + (x[r+1][c] - x[r][c]) / 2, y - (gap * bending), x[r+1][c], y); // draws a curve
      }
    }
  }
}

public void mousePressed() {
  changeGap = false; // if changeGap is false, you can change image color when drawImage function is called
  drawImage();  
  useKeys = true; // use keys only when image is ready
}

public void keyPressed() {  
  if (useKeys) {    

    background(30); // draws background again, to not overlap changes

    // changes raws weight
    if ((key == 'r'  || key == 'R') && rawsWeight > 0.1f)
      rawsWeight-=0.1f;
    if ((key == 's' || key == 'S') && rawsWeight < 3)
      rawsWeight+=0.1f;

    // changes curves bending
    if ((key == 'b' || key == 'B') && bending > 0.1f)
      bending-=0.1f;    
    if ((key == 'c' || key == 'C') && bending < 1)
      bending+=0.1f;

    // changes the gap between the raws
    if ((key == 'f' || key == 'F') && gapIndex > height / 10 /* minimum gap */ ) {
      gapIndex-=10;
      changeGap = true; // is changeGap is true, image color doen't change when drawImage is called
      setup();
      drawImage();
    }
    if ((key == 'g' || key == 'G') && gapIndex < height / 5 /* maximum gap */ ) {
      gapIndex+=10;
      changeGap = true; 
      setup();
      drawImage();
    }

    drawLines(gap);
    drawCurves(x, gap);

    image(img, width / 2, 0);
  }
}

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Illusion3D" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
