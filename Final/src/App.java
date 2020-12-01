import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.util.Random;

public class App extends JFrame{
	public App() {
		setTitle("[성준혁]사격 게임");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		GamePanel p = new GamePanel();
		setContentPane(p);
		getContentPane().setBackground(Color.BLACK);

		setSize(500,500);
		setResizable(false);		
		setVisible(true);
		
		// 컨텐트팬을 구성하는 모든 컴포넌트들의 위치와 크기가 결정된 후 게임을 시작하게 한다.
		p.startGame();
	}
	
	public static void main(String [] args) {
        new App();	
    }
}

class GamePanel extends JPanel {
	private int i=0;
	private int score = 0;
	final int MAX = 3;
	private TargetThread targetThread=null;
	private JLabel baseLabel;
	private JLabel targetLabel;
	private JLabel label;
	class Bullet {
		private JLabel bulletLabel;
		private boolean check;
		private int id;

		Bullet(ImageIcon img_bullet, int id){
			this.bulletLabel = new JLabel(img_bullet);
			bulletLabel.setSize(img_bullet.getIconWidth(),img_bullet.getIconWidth());
			this.id = id;
			this.check = true;
		}

		public void setID(int id) {
			this.id = id;
		}
		public int getID() {
			return id;
		}

		public void setCheck(boolean bool){
			this.check = bool;
		}  
		public boolean getCheck(){
			return this.check;
		}  
	}
	Bullet bullet[] = new Bullet[MAX];
	
	
	public GamePanel() {
		setLayout(null);

		ImageIcon img_base = new ImageIcon("images/base.png");
		baseLabel = new JLabel(img_base);
		baseLabel.setSize(img_base.getIconWidth(),img_base.getIconWidth());

		ImageIcon img_target = new ImageIcon("images/target.jpg");
		targetLabel = new JLabel(img_target);
		targetLabel.setSize(img_target.getIconWidth(),img_target.getIconWidth());

		ImageIcon img_bullet = new ImageIcon("images/bullet.png");
		for (int i=0; i<MAX; i++) {
			bullet[i] = new Bullet(img_bullet, i);
			add(bullet[i].bulletLabel);
		}

		// JLabel stage_label = new JLabel("");
		// stage_label.setForeground(Color.WHITE);
		// stage_label.setSize(100, 100);
		// stage_label.setLocation(250, 250);
		// add(stage_label);
		// MonitorThread mtThread = new MonitorThread(stage_label);
		// mtThread.start();
		


		label = new JLabel("0");
		label.setForeground(Color.GREEN);
		label.setSize(30, 15);
		label.setLocation(450, 0);
		add(label);
		add(baseLabel);
		add(targetLabel);
	}
	
	public void startGame() {
		baseLabel.setLocation(this.getWidth()/2-20, this.getHeight()-40);
		
		for(int i=0; i<MAX; i++)
			bullet[i].bulletLabel.setLocation(i*10, 0);		

		targetLabel.setLocation(0, 0);
		
		targetThread = new TargetThread(targetLabel);
		targetThread.start();
		
		baseLabel.setFocusable(true); // baseLabel이 포커스를 받을 수 있도록 설정 
		baseLabel.requestFocus(); // baseLabel에 포커스를 주어 <Enter>키가 입력되면 아래의 키 리스너가 작동하게 함
		baseLabel.addKeyListener(new KeyAdapter() {
			private BulletThread  bulletThread = null;
			public void keyPressed(KeyEvent e) {
				if(e.getKeyChar() == '\n') {
					if(bullet[i].getCheck()) {	//총알이 있을 때
						bullet[i].bulletLabel.setLocation(baseLabel.getX()+15, baseLabel.getY());
						bulletThread = new BulletThread(bullet, bullet[i], targetLabel, targetThread, label);
						bulletThread.start();
						// System.out.println(i);
						i+=1; i%=MAX;
					}		
					else {	//총알 없음
						

					}
					//if(bulletThread==null || !bulletThread.isAlive()) {
					//}
				}

				switch (e.getKeyCode()) {
					case KeyEvent.VK_LEFT:
						if (baseLabel.getX() > 10)
							baseLabel.setLocation(baseLabel.getX()-15, baseLabel.getY());
						break;
					case KeyEvent.VK_RIGHT:
						if (baseLabel.getX() < 500-60)
							baseLabel.setLocation(baseLabel.getX()+15, baseLabel.getY());
						break;
				}
			}
		});
	}

	// class MonitorThread extends Thread {
	// 	private JLabel label;
	// 	private int stage=1;
	// 	public MonitorThread(JLabel label) {
	// 		this.label = label;
	// 		label.setLocation(0, 0);
	// 		label.getParent().repaint();
	// 	}
	// 	public void run() {
	// 		while(true) {
	// 			try {
	// 				sleep(10);
	// 				if(score > 0 && score%50 == 0) {
	// 					stage++;
	// 					label.setText("Stage "+stage);
	// 				}
	// 			} catch (Exception e) {
	// 				//TODO: handle exception
	// 			}
	// 		}
	// 	}
	// }

	class TargetThread extends Thread {
		private JComponent target;
		private int rand_num=0;
		private int xval=0;
		private int yval=0;
		Random random = new Random();

		public TargetThread(JComponent target) {
			this.xval = 5;
			this.target = target;
			target.setLocation(0, 0);
			target.getParent().repaint();
		}
		
		public void left() {
			this.xval = 5;
			this.yval = 0;
		}
		public void right() {
			this.xval = -5;
			this.yval = 0;
		}
		public void down() {
			this.xval = 0;
			this.yval = 5;
		}

		public void rand_gen() {
			rand_num = random.nextInt(3);
			switch(rand_num) {
				case 0:
					// System.out.println("left");
					left();
					target.setLocation(-60, random.nextInt(300));
					break;
				case 1:
					// System.out.println("right");
					right();
					target.setLocation(500, random.nextInt(300));
					break;
				case 2:
					// System.out.println("down");
					down();
					target.setLocation(random.nextInt(440)+10, -60);
					break;
			}
		}
		
		public void run() {
			while(true) {
				int x = target.getX()+xval;
				int y = target.getY()+yval;
				if(rand_num == 0 && x > GamePanel.this.getWidth()) // left
					rand_gen();
					// target.setLocation(-10,0);
				else if (rand_num == 1 && x < -60)
					rand_gen();
					// target.setLocation(500,0);
				else if (rand_num == 2 && y > 500)
					rand_gen();
					// target.setLocation(random.nextInt(450)+10, 0);
				else	//pass
					target.setLocation(x, y);

				target.getParent().repaint();
				try {
					sleep(20-score/50);
				}
				catch(InterruptedException e) {
					// the case of hit by a bullet
					rand_gen();
					
					target.getParent().repaint();
					try {
						sleep(500); // 0.5초 기다린 후에 계속한다.
					}catch(InterruptedException e2) {}					
				}
			}
		}			
	}

	
	class BulletThread extends Thread {
		private JComponent target;
		private Bullet bullet;
		private Bullet bulletArr[];
		private Thread targetThread;
		private JLabel label;
		
		public BulletThread(Bullet bulletArr[], Bullet bullet, JComponent target, Thread targetThread, JLabel label) {
			this.bulletArr = bulletArr; 
			this.bullet = bullet;
			this.target = target;
			this.targetThread = targetThread;	
			this.label = label;			
		}
		
		public void run() {
			bullet.setCheck(false); //쐈음
			while(true) {
				// 명중하였는지 확인
				if(hit()) {
					targetThread.interrupt();
					// bullet.setLocation(bullet.getParent().getWidth()/2 - 5, bullet.getParent().getHeight()-60);						
					bullet.bulletLabel.setLocation(-10, 0);
					score += 10;
					label.setText(Integer.toString(score));
					if(bullet.getID()==MAX-1) {
						for(int i=0; i<MAX; i++) {
							bulletArr[i].bulletLabel.setLocation(i*10, 0);
							bulletArr[i].setCheck(true);
						}		
					}
						
					return;
				}
				else{
					int x = bullet.bulletLabel.getX() ;
					int y = bullet.bulletLabel.getY() - 5;
					if(y < 0) {
						// bullet.setLocation(bullet.getParent().getWidth()/2 - 5, bullet.getParent().getHeight()-60);
						bullet.bulletLabel.setLocation(-10, 0);
						bullet.bulletLabel.getParent().repaint();
						if(bullet.getID()==MAX-1)
							for(int i=0; i<MAX; i++) {
								bulletArr[i].bulletLabel.setLocation(i*10, 0);	
								bulletArr[i].setCheck(true);
							}
						return; // thread ends
					}
					bullet.bulletLabel.setLocation(x, y);
					bullet.bulletLabel.getParent().repaint();
				}
				try {
					sleep(10);
				}
				catch(InterruptedException e) {}
			}
		}
		
		private boolean hit() {
			if(targetContains(bullet.bulletLabel.getX(), bullet.bulletLabel.getY()) || 
					targetContains(bullet.bulletLabel.getX() + bullet.bulletLabel.getWidth() - 1, bullet.bulletLabel.getY()) ||
					targetContains(bullet.bulletLabel.getX() + bullet.bulletLabel.getWidth() - 1, bullet.bulletLabel.getY()+bullet.bulletLabel.getHeight() - 1) ||
					targetContains(bullet.bulletLabel.getX(), bullet.bulletLabel.getY()+bullet.bulletLabel.getHeight() - 1))
				return true;
			else
				return false;					
		}
		
		private boolean targetContains(int x, int y) {
			if(((target.getX() <= x) && (target.getX() + target.getWidth() - 1 >= x)) &&
					((target.getY() <= y)&& (target.getY() + target.getHeight() - 1 >= y))) {
				return true;
			}
			else
				return false;
			
		}
	}	
}