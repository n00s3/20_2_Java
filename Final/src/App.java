import java.awt.Color;
import java.awt.Font;
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
	private boolean reload = true;
	final int MAX = 5;
	
	private TargetThread targetThread=null;
	private JLabel baseLabel;
	private JLabel targetLabel;
	private JLabel label;
	private JLabel label_txt;
	private JLabel label_stage;
	private JLabel bullets[] = new JLabel[MAX];
	class Bullet {
		private JLabel bulletLabel;
		private int id;

		Bullet(ImageIcon img_bullet, int id){
			this.bulletLabel = new JLabel(img_bullet);
			bulletLabel.setSize(img_bullet.getIconWidth(),img_bullet.getIconWidth());
			setID(id);
		}

		public void setID(int id) {
			this.id = id;
		}
		public int getID() {
			return id;
		}
	}
	Bullet bullet[] = new Bullet[MAX];
	
	
	public GamePanel() {
		setLayout(null);

		ImageIcon img_base = new ImageIcon("images/base.jpg");
		baseLabel = new JLabel(img_base);
		baseLabel.setSize(img_base.getIconWidth(),img_base.getIconWidth());

		ImageIcon img_target = new ImageIcon("images/target.jpg");
		targetLabel = new JLabel(img_target);
		targetLabel.setSize(img_target.getIconWidth(),img_target.getIconWidth());
		targetLabel.setSize(50, 50);

		ImageIcon img_bullet = new ImageIcon("images/bullet.png");
		for (int i=0; i<MAX; i++) {
			bullet[i] = new Bullet(img_bullet, i);
			add(bullet[i].bulletLabel);
			bullets[i] = new JLabel(img_bullet);
			bullets[i].setSize(img_bullet.getIconWidth(),img_bullet.getIconWidth());
			add(bullets[i]);
		}

		label_txt = new JLabel("RELOAD");
		label_txt.setForeground(Color.WHITE);
		label_txt.setSize(50, 10);
		label_txt.setLocation(0, 0);
		label_txt.setVisible(false);

		label_stage = new JLabel("");
		label_stage.setForeground(Color.WHITE);
		label_stage.setSize(300, 300);
		label_stage.setFont(new Font("Gothic", Font.BOLD, 24));
		label_stage.setLocation(185, 50);
		label_stage.setVisible(false);

		label = new JLabel("0");
		label.setForeground(Color.GREEN);
		label.setSize(30, 15);
		label.setLocation(450, 0);
		
		add(label_txt);
		add(label_stage);
		add(label);
		add(baseLabel);
		add(targetLabel);
	}
	
	public void startGame() {
		baseLabel.setLocation(this.getWidth()/2-20, this.getHeight()-40);
		
		for(int i=0; i<MAX; i++){
			bullet[i].bulletLabel.setLocation(-10, -10);
			bullets[i].setLocation(i*10, 0);		

		}
		targetLabel.setLocation(0, 0);
		
		targetThread = new TargetThread(targetLabel);
		targetThread.start();
		
		baseLabel.setFocusable(true); // baseLabel이 포커스를 받을 수 있도록 설정 
		baseLabel.requestFocus(); // baseLabel에 포커스를 주어 <Enter>키가 입력되면 아래의 키 리스너가 작동하게 함
		baseLabel.addKeyListener(new KeyAdapter() {
			private BulletThread  bulletThread = null;
			public void keyPressed(KeyEvent e) {
				if(e.getKeyChar() == '\n' || e.getKeyChar() == 32 && reload == true) { //enter + space bar
						bullet[i].bulletLabel.setLocation(baseLabel.getX()+15, baseLabel.getY());
						bulletThread = new BulletThread(bullet[i], targetLabel, targetThread, label);
						bulletThread.start();
						i+=1; i%=MAX;
					}
				
				switch (e.getKeyCode()) {
					case KeyEvent.VK_LEFT:
						if (baseLabel.getX() > 10)
							baseLabel.setLocation(baseLabel.getX()-15, baseLabel.getY());
						break;
					case KeyEvent.VK_RIGHT:
						if (baseLabel.getX() < baseLabel.getParent().getWidth()-60)
							baseLabel.setLocation(baseLabel.getX()+15, baseLabel.getY());
						break;
				}
			}
		});
	}

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
			rand_num = random.nextInt(4);
			switch(rand_num) {
				case 0:
					left();
					target.setLocation(-60, random.nextInt(300));
					break;
				case 1:
					right();
					target.setLocation(target.getParent().getWidth(), random.nextInt(300));
					break;
				case 2: 
				case 3:
					down();
					target.setLocation(random.nextInt(target.getParent().getWidth()-50), -60);
					break;
			}
		}
		
		public void run() {
			while(true) {
				int x = target.getX()+xval;
				int y = target.getY()+yval;
				if (x > GamePanel.this.getWidth() || x < -60 || y > target.getParent().getHeight())
					rand_gen();
				else	//pass
					target.setLocation(x, y);
				target.getParent().repaint();
				
				try {
					sleep(20-(score/50));	//stage
				}
				catch(InterruptedException e) {
					rand_gen();
					target.getParent().repaint();
					try {
						sleep(50); // 0.05초 기다린 후에 계속한다.
					}catch(InterruptedException e2) {}					
				}
			}
		}			
	}
	class StageThread extends Thread {
		int stage;

		public StageThread(int score) {
			stage = score/50;
		}

		public void run() {
			label_stage.setText("<html>STAGE : " + (stage+1) + "<br>SPEED UP!</html>");
			label_stage.setVisible(true);
			try {
				sleep(2000);
			} catch (Exception e) {

			}
			label_stage.setVisible(false);
		}
	}


	class ReloadThread extends Thread {
		public void run() {
			reload = false;
			label_txt.setVisible(true);
			try {
				sleep(1000);
			} catch (Exception e) {

			}
			label_txt.setVisible(false);
			
			for(int i=0; i<MAX; i++) {
				bullets[i].setVisible(true);
			}
			reload = true;
		}
	}

	
	class BulletThread extends Thread {
		private JComponent target;
		private Bullet bullet;
		private Thread targetThread;
		private JLabel label;
		
		public BulletThread(Bullet bullet, JComponent target, Thread targetThread, JLabel label) {
			this.bullet = bullet;
			this.target = target;
			this.targetThread = targetThread;	
			this.label = label;			
		}
		
		public void run() {
			bullets[bullet.getID()].setVisible(false);

			if(bullet.getID()==MAX-1) {
				ReloadThread rt = new ReloadThread();
				rt.start();
			}

			while(true) {
				// 명중하였는지 확인
				if(hit()) {
					targetThread.interrupt();
					bullet.bulletLabel.setLocation(-10, 0);
					score += 10;
					if (score > 0 && score % 50 == 0) {
						StageThread st = new StageThread(score);
						st.start();
					}

					label.setText(Integer.toString(score));
						
					return;
				}
				else{
					int x = bullet.bulletLabel.getX() ;
					int y = bullet.bulletLabel.getY() - 5;
					if(y < 0) {
						bullet.bulletLabel.setLocation(-10, 0);
						bullet.bulletLabel.getParent().repaint();

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