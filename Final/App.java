import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Ch12_Ex05 extends JFrame{
	public Ch12_Ex05() {
		setTitle("[������]��� ����");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		GamePanel2 p = new GamePanel2();
		setContentPane(p);
		setSize(300,300);
		setResizable(false);		
		setVisible(true);
		
		// ����Ʈ���� �����ϴ� ��� ������Ʈ���� ��ġ�� ũ�Ⱑ ������ �� ������ �����ϰ� �Ѵ�.
		p.startGame();
	}
	
	public static void main(String [] args) {	new Ch12_Ex05();	}
}

class GamePanel2 extends JPanel {
	private TargetThread targetThread=null;
	private JLabel baseLabel = new JLabel();
	private JLabel bulletLabel = new JLabel();
	private JLabel targetLabel;
	
	public GamePanel2() {
		setLayout(null);
	
		baseLabel.setSize(40,40);
		baseLabel.setOpaque(true);
		baseLabel.setBackground(Color.BLACK);

		ImageIcon img = new ImageIcon("images/chicken.jpg");
		targetLabel = new JLabel(img);
		targetLabel.setSize(img.getIconWidth(),img.getIconWidth());

		bulletLabel.setSize(10,10);
		bulletLabel.setOpaque(true);
		bulletLabel.setBackground(Color.RED);
		add(baseLabel);
		add(targetLabel);
		add(bulletLabel);
	}
	
	public void startGame() {
		baseLabel.setLocation(this.getWidth()/2-20, this.getHeight()-40);
		bulletLabel.setLocation(this.getWidth()/2 - 5, this.getHeight()-50);			
		targetLabel.setLocation(0, 0);
		
		targetThread = new TargetThread(targetLabel);
		targetThread.start();
		
		baseLabel.setFocusable(true); // baseLabel�� ��Ŀ���� ���� �� �ֵ��� ���� 
		baseLabel.requestFocus(); // baseLabel�� ��Ŀ���� �־� <Enter>Ű�� �ԷµǸ� �Ʒ��� Ű �����ʰ� �۵��ϰ� ��
		baseLabel.addKeyListener(new KeyAdapter() {
			private BulletThread  bulletThread = null;
			public void keyPressed(KeyEvent e) {
				if(e.getKeyChar() == '\n') {
					if(bulletThread==null || !bulletThread.isAlive()) {
						bulletThread = new BulletThread(bulletLabel, targetLabel, targetThread);
						bulletThread.start();
					}
				}
			}
		});
	}
	
	class TargetThread extends Thread {
		private JComponent target;
		
		public TargetThread(JComponent target) {
			this.target = target;
			target.setLocation(0, 0);
			target.getParent().repaint();
		}	
		public void run() {
			while(true) {
				int x = target.getX()+5;
				int y = target.getY();
				if(x > GamePanel2.this.getWidth()) 
					target.setLocation(0,0);
				else 
					target.setLocation(x, y);

				target.getParent().repaint();
				try {
					sleep(20);
				}
				catch(InterruptedException e) {
					// the case of hit by a bullet
					target.setLocation(0, 0);
					target.getParent().repaint();
					try {
						sleep(500); // 0.5�� ��ٸ� �Ŀ� ����Ѵ�.
					}catch(InterruptedException e2) {}					
				}
			}
		}			
	}
	
	class BulletThread extends Thread {
		private JComponent bullet, target;
		private Thread targetThread;
		
		public BulletThread(JComponent bullet, JComponent target, Thread targetThread) {
			this.bullet = bullet;
			this.target = target;
			this.targetThread = targetThread;				
		}
		
		public void run() {
			while(true) {
				// �����Ͽ����� Ȯ��
				if(hit()) {
					targetThread.interrupt();
					bullet.setLocation(bullet.getParent().getWidth()/2 - 5, bullet.getParent().getHeight()-50);						
					return;
				}
				else {
					int x = bullet.getX() ;
					int y = bullet.getY() - 5;
					if(y < 0) {
						bullet.setLocation(bullet.getParent().getWidth()/2 - 5, bullet.getParent().getHeight()-50);
						bullet.getParent().repaint();
						return; // thread ends
					}
					bullet.setLocation(x, y);
					bullet.getParent().repaint();
				}
				try {
					sleep(20);
				}
				catch(InterruptedException e) {}
			}
		}
		
		private boolean hit() {
			if(targetContains(bullet.getX(), bullet.getY()) || 
					targetContains(bullet.getX() + bullet.getWidth() - 1, bullet.getY()) ||
					targetContains(bullet.getX() + bullet.getWidth() - 1, bullet.getY()+bullet.getHeight() - 1) ||
					targetContains(bullet.getX(), bullet.getY()+bullet.getHeight() - 1))
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