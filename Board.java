package collision;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener{
	private Timer timer;
	private Craft craft;
	private ArrayList aliens;
	private boolean ingame;
	private int bWidth;
	private int bHeight;
	private int lives = 3;
	
	private int[][] pos = {
			{2380, 29}, {2500, 59}, {1380, 89},
			{780, 109}, {580, 139}, {680, 239},
			{790, 259}, {760, 50}, {790, 150},
	        {980, 209}, {560, 45}, {510, 70},
	        {930, 159}, {590, 80}, {530, 60},
	        {940, 59}, {990, 30}, {920, 200},
	        {900, 259}, {660, 50}, {540, 90},
	        {810, 220}, {860, 20}, {740, 180},
	        {820, 128}, {490, 170}, {700, 30}
	};
	
	public Board(){
		addKeyListener(new TAdapter());
		setFocusable(true);
		setBackground(Color.BLACK);
		setDoubleBuffered(true);
		ingame = true;
		
		setSize(400, 300);
		
		craft = new Craft();
		
		initAliens();
		
		timer = new Timer(5, this); // sets timer to 5ms
		timer.start();
	}

	public void addNotify(){
		super.addNotify();
		bWidth = getWidth();
		bHeight = getHeight();
	}
	
	public void initAliens(){
		aliens = new ArrayList();
		
		for (int i = 0; i < pos.length; i++){
			aliens.add(new Alien(pos[i][0], pos[i][1]));
		}
	}
	
	public void paint(Graphics g){
		super.paint(g);
		
		if (ingame){
		
			Graphics2D g2d = (Graphics2D) g;
			if (craft.isVisible()){
				g2d.drawImage(craft.getImage(), craft.getX(), craft.getY(), this);
			}
			
			ArrayList ms = craft.getMissiles();
		
			for (int i = 0; i < ms.size(); i++){
				Missile m = (Missile) ms.get(i);
				g2d.drawImage(m.getImage(), m.getX(), m.getY(), this);
			}
			
			for (int i = 0; i < aliens.size(); i++){
				Alien a = (Alien)aliens.get(i);
				if (a.isVisible())
					g2d.drawImage(a.getImage(), a.getX(), a.getY(), this);
			}
			
			g2d.setColor(Color.WHITE);
			g2d.drawString("Aliens left: " + aliens.size(), 5, 15);
			g2d.drawString("Lives left: " + lives, 5, 30);
		} else {
			String msg = "Game Over";
			Font small = new Font("Helvetica", Font.BOLD, 14);
			FontMetrics metr = this.getFontMetrics(small);
			
			g.setColor(Color.white);
			g.setFont(small);
			g.drawString(msg, (bWidth - metr.stringWidth(msg)) / 2,
						bHeight / 2);
		}
		
		Toolkit.getDefaultToolkit().sync();
		g.dispose();
	}
	
	/*
	 * this method iterates through all the missiles and aliens
	 * and checks if the aliens havent been hit, if they have
	 * it removes them
	 */
	public void actionPerformed(ActionEvent e){ // called every 5ms
		
		if (aliens.size() == 0){
			ingame = false;
		}
		
		ArrayList ms = craft.getMissiles();
		
		for (int i = 0; i < ms.size(); i++){
			Missile m = (Missile) ms.get(i);
			if (m.isVisible()) m.move();
			else ms.remove(i);
		}
		
		for (int i = 0; i < aliens.size(); i++){
			Alien a = (Alien) aliens.get(i);
			if (a.isVisible()) a.move();
			else aliens.remove(i);
		}
		
		craft.move();
		checkCollisions();
		repaint();
	}
	
	/*
	 * creates rectangles of all the objects and 
	 * checks if either one of the aliens hits 
	 * the craft or one of the missiles hits the
	 * aliens
	 */
	public void checkCollisions(){
		
		Rectangle r3 = craft.getBounds();
		
		for (int j = 0; j < aliens.size(); j++){
			Alien a = (Alien) aliens.get(j);
			Rectangle r2 = a.getBounds();
			
			if (r3.intersects(r2)){
				lives -= 1;
				if (lives < 0){
					craft.setVisible(false);
					a.setVisible(false);
					ingame = false;
				}
				else {
					craft = new Craft();
				}
			}
		}
		
		ArrayList ms = craft.getMissiles();
		
		for (int i = 0; i < ms.size(); i++){
			Missile m = (Missile) ms.get(i);
			
			Rectangle r1 = m.getBounds();
			
			for (int j = 0; j < aliens.size(); j++){
				Alien a = (Alien) aliens.get(j);
				Rectangle r2 = a.getBounds();
				
				if (r1.intersects(r2)){
					m.setVisible(false);
					a.setVisible(false);
				}
			}
		}
	}
	
	private class TAdapter extends KeyAdapter {
		
		public void keyReleased(KeyEvent e){
			craft.keyReleased(e);
		}
		public void keyPressed(KeyEvent e){
			craft.keyPressed(e);
		}
	}
}
