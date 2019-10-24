package yys.script.hf;

import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main extends JFrame implements ActionListener{
	/**
	 * �汾��
	 */
	private static final long serialVersionUID = -135885139973224087L;
	private String shuoming = "1.��СĬ��Ϊ�ٷ�������"
			+ "\r\n2.Ŀǰ��֧�ֵ���ˢ����"
			+ "\r\n4.����ʱ��ʼΪ0��ֹͣ��"
			+ "\r\n5.�Թ���Ա������У�������Ȩ������꣡"
			+ "\r\n               author:   hf"
			+ "\r\n               date  :    2019��7��1��";
	
	JButton startOrPause = new JButton();//��ʼ��ͣ��ť
	JLabel actionType = new JLabel("ս�����ͣ�"); 
	JComboBox<String> act = new JComboBox<String>(new String[]{"����&����","̽��","����","ҵԭ��"});
	JLabel yaoqing = new JLabel("�������룺"); 
	JComboBox<String> yq = new JComboBox<String>(new String[]{"��","��"});
	JLabel chaoguiwang = new JLabel("��������"); 
	JComboBox<String> cgw = new JComboBox<String>(new String[]{"��","��"});
	JLabel fenbianlv = new JLabel("�ֱ��ʣ�");
	JComboBox<String> fbl = new JComboBox<String>(new String[]{"1280*720","960*540"});
	JLabel daojishi = new JLabel("����ʱ��"); 
	JTextField hh=new JTextField();
	JLabel maohao1 = new JLabel(":"); 
	JTextField mm=new JTextField();
	JLabel maohao2 = new JLabel(":"); 
	JTextField ss=new JTextField();
 	JTextArea outputinfo = new JTextArea(1, 17);
	Robot robot = null;
	String absolutePath = "";
	Map<String, BufferedImage> imageMaps = new HashMap<String, BufferedImage>();
	Map<String,Map<String, BufferedImage>> imagesMap = new HashMap<String,Map<String, BufferedImage>>(); 
	
	Map<String,int[]> position = new HashMap<String,int[]>();
	int mouseSpeed = 20;
	public boolean isstart = false;
	int nopersonTimes = 0;
	int nopersonTimesMax = 60;
	int mosex = 0;
	int mosey = 0;
	int totalMinus = 0;
	//����ѭ���߳�
	Thread t = new Thread(){
		public void run() {
			while(true){
				try {
					Thread.sleep(getNum(200, 400));
					if(isstart){
						doPlayAction();//��ʼˢͼ
					}
				} catch (Exception e) {
					e.printStackTrace();
					//TODO ���������Ϣ��
					outputinfo.setText(e.getMessage());
					doPause();//��ͣˢͼ
				}
			}
		};
	};
	
	//��������ʱ�߳�
	Thread timemanager = new Thread(){
		public void run() {
			while(true){
				try {
					Thread.sleep(1000);
					doTimeCountDown();
				} catch (Exception e) {
					//TODO ���������Ϣ��
					outputinfo.setText(e.getMessage());
				}
			}
		}

	};
	
	//�����ű���壺��ʼ����ͣ��ˢ���꣬ˢ���ѡ�
	Main(){
		super();
		Color cc =new Color((int) (Math.random()*255),(int) (Math.random()*255),(int) (Math.random()*255));
		SwingUtilities.updateComponentTreeUI(getContentPane()); 
		setSize(200, 500);//���ÿ��
		setLocationRelativeTo(null);//������ʾλ��
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//���ùرն���
		setVisible(true);//��ʾ
		setResizable(false);//�����Ƿ���Ե�����С
		setLayout(null);//���ò��֡�
		setComponent();//�������������¼���
		addComponent();//��Ӱ�ť����ѡ����
		this.getContentPane().setBackground(cc);
		this.getContentPane().setForeground(cc);
		startOrPause.setBackground(cc);
		act.setBackground(cc);
		yq.setBackground(cc);
		cgw.setBackground(cc);
		fbl.setBackground(cc);
		hh.setBackground(cc);
		mm.setBackground(cc);
		ss.setBackground(cc);
		outputinfo.setBackground(cc);
		//setAlwaysOnTop(true);//���ʼ������Ļ��ǰ��
		hh.setText("00");
		mm.setText("00");
		ss.setText("00");
		t.start();
		timemanager.start();
		try {
			absolutePath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
			absolutePath = java.net.URLDecoder.decode(absolutePath, "UTF-8");
			absolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("/"));
			imagesMap.put("960*540", getImages("960-540"));
			imagesMap.put("1280*720", getImages("1280-720"));
			imageMaps = imagesMap.get("1280*720");
			robot = new Robot();
		} catch (Exception e) {
			System.exit(0);
		}
		//��Ӽ����¼���
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
		    public void eventDispatched(AWTEvent event) {
		        if (((KeyEvent) event).getID() == KeyEvent.KEY_RELEASED) {
		        	if(((KeyEvent) event).getKeyCode() == 32){//���ո�� ��ʼ/��ͣ
		        		startOrPause.doClick(30);
		        	}
		        }
		    }
		}, AWTEvent.KEY_EVENT_MASK);
		outputinfo.setText(shuoming);
	}
	//������ʾʱ����
	private void doTimeCountDown() {
		if(isstart && totalMinus>0 ){//ʱ�����0,���һ
			totalMinus --;
			hh.setText((totalMinus/ 60 / 60 % 60) >= 10 ? ""+(totalMinus/ 60 / 60 % 60) : "0"+(totalMinus/ 60 / 60 % 60));
			mm.setText((totalMinus / 60 % 60)>=10 ? ""+(totalMinus / 60 % 60):"0"+(totalMinus / 60 % 60));
			ss.setText((totalMinus % 60)>=10 ? ""+(totalMinus % 60) : "0"+(totalMinus % 60));
			if(totalMinus == 0){
				startOrPause.doClick(30);
			}
			
		}
		
	};
	/**
	 * ��ȡkeyimages
	 * @return
	 */
	private Map<String, BufferedImage> getImages(String fblpath) {
		//��ȡ��ťͼƬ��
		Map<String,BufferedImage> imagesMap = new HashMap<String,BufferedImage>();
		imagesMap.put("ʤ��", getImageFromPath("/images/"+fblpath+"/shengli.png"));
		imagesMap.put("ʧ��", getImageFromPath("/images/"+fblpath+"/shibai.png"));
		imagesMap.put("��ս", getImageFromPath("/images/"+fblpath+"/tiaozhan.png"));
		imagesMap.put("����", getImageFromPath("/images/" + fblpath + "/zhanlipin.png"));
		imagesMap.put("̽��", getImageFromPath("/images/" + fblpath + "/tansuo.png"));
	    imagesMap.put("���", getImageFromPath("/images/" + fblpath + "/daguai.png"));
	    imagesMap.put("�˳�", getImageFromPath("/images/" + fblpath + "/tuichu.png"));
	    imagesMap.put("ȷ��", getImageFromPath("/images/" + fblpath + "/queren.png"));
	    imagesMap.put("����", getImageFromPath("/images/" + fblpath + "/fanhui.png"));
		imagesMap.put("���", getImageFromPath("/images/"+fblpath+"/jinbi.png"));
		return imagesMap;
	}

	//��ʼ��������
	private void setComponent() {
		//���ð�ť����
		startOrPause.setText("��ʼ");
		startOrPause.setPreferredSize(new Dimension(100, 50));//���ð�ť��С
		startOrPause.setBounds(25, 10, 150, 50);
		startOrPause.setFont(new Font("����", Font.BOLD,25));
		startOrPause.addActionListener(this);
		//�ֱ���
		fenbianlv.setBounds(5, 73, 65, 30);
		fenbianlv.setFont(new Font("����", Font.BOLD,15));
		fbl.setBounds(80, 70, 100, 30);
		fbl.setFont(new Font("����", Font.BOLD,13));
		fbl.addActionListener(this);
		//��������
		yaoqing.setBounds(5, 113, 80, 30);
		yaoqing.setFont(new Font("����", Font.BOLD,15));
		yq.setBounds(80, 110, 100, 30);
		yq.setFont(new Font("����", Font.BOLD,15));
		
		//������
		chaoguiwang.setBounds(5, 153, 80, 30);
		chaoguiwang.setFont(new Font("����", Font.BOLD,15));
		cgw.setBounds(80, 150, 100, 30);
		cgw.setFont(new Font("����", Font.BOLD,15));
		
		//ս������
		actionType.setBounds(5, 190, 80, 30);
		actionType.setFont(new Font("����", Font.BOLD,15));
		act.setBounds(80, 187, 108, 30);
		act.setFont(new Font("����", Font.BOLD,15));

		//����ʱ
		daojishi.setBounds(5, 223, 85, 30);
		daojishi.setFont(new Font("����", Font.BOLD,15));
		hh.setBounds(80, 223, 30, 30);
		hh.setFont(new Font("����", Font.BOLD,15));
		maohao1.setBounds(110, 223, 10, 30);
		maohao1.setFont(new Font("����", Font.BOLD,15));
		mm.setBounds(120, 223, 30, 30);
		mm.setFont(new Font("����", Font.BOLD,15));
		maohao2.setBounds(150, 223, 10, 30);
		maohao2.setFont(new Font("����", Font.BOLD,15));
		ss.setBounds(160, 223, 30, 30);
		ss.setFont(new Font("����", Font.BOLD,15));
		ss.setEditable(false);
		//�����Ϣ
		outputinfo.setBounds(5, 275, 185, 170);
		outputinfo.setEditable(true);
		outputinfo.setOpaque(false);
		outputinfo.setLineWrap(true);
	}
	//�����������
	private void addComponent() {
		getContentPane().add(startOrPause);
		getContentPane().add(fenbianlv);
		getContentPane().add(fbl);
		getContentPane().add(chaoguiwang);
		getContentPane().add(cgw);
		getContentPane().add(actionType);
		getContentPane().add(act);
		getContentPane().add(yaoqing);
		getContentPane().add(yq);
		getContentPane().add(daojishi);
		getContentPane().add(hh);
		getContentPane().add(maohao1);
		getContentPane().add(mm);
		getContentPane().add(maohao2);
		getContentPane().add(ss);
		getContentPane().add(outputinfo);
	}


	//�¼�����
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == startOrPause){
			if("��ʼ".equals(startOrPause.getText())){//δ����
				doStart();
			}else if("��ͣ".equals(startOrPause.getText())){
				doPause();
			}
		}else if(e.getSource() == fbl){
			if("960*540".equals(fbl.getSelectedItem())){
				imageMaps = imagesMap.get("960*540");
				outputinfo.setText("�л����ֱ��ʣ�960*540");
			}else if("1280*720".equals(fbl.getSelectedItem())){
				imageMaps = imagesMap.get("1280*720");
				outputinfo.setText("�л����ֱ��ʣ�1280*720");
			}
		}
		
	}
	
	/**
	 * ����ˢͼѭ����
	 */
	public void doStart(){
		isstart = true;
		nopersonTimes = 0;
		startOrPause.setText("��ͣ");
		outputinfo.setText("��ʼ��Ŷ��");
		fbl.setEnabled(false);
		hh.setEditable(false);
		mm.setEditable(false);
		totalMinus = getTotalMinus();
	}


	/**
	 * ֹͣˢͼѭ����
	 */
	public void doPause(){
		isstart = false;
		nopersonTimes = 0;
		startOrPause.setText("��ʼ");
		fbl.setEnabled(true);
		hh.setEditable(true);
		mm.setEditable(true);
		outputinfo.setText(shuoming);
	}

	/**
	 * ��ʼˢͼ
	 */
	private void doPlayAction(){
		//����
		BufferedImage screenShotImage = getScreenShot();
		if(screenShotImage == null || imageMaps == null) return;
		if("����&����".equals(act.getSelectedItem()))
		{
			if(clickTiaoZhan(screenShotImage)) return;//��ʼ��ս
			if(clickJixuTiaoZhan(screenShotImage)) return;//������ս
			if(clickShiBai(screenShotImage)) return;//ʧ��
		}
		else if("̽��".equals(act.getSelectedItem()))
		{
			if(clickTanSuo(screenShotImage)) return;//��ʼ��ս
			if(clickDaGuai(screenShotImage)) return;//ʤ��ͼƬ
			if(clickQueRenTuiChu(screenShotImage)) return;//ʧ��ͼƬ
			if(clickJixuTiaoZhan(screenShotImage)) return;//ʤ��ͼƬ
			if(clickShiBai(screenShotImage)) return;//ʧ��
		}
	}
	
	  private boolean clickDaGuai(BufferedImage screenShotImage)
	  {
	    BufferedImage daguaiButton = (BufferedImage)this.imageMaps.get("���");
	    if (findPic(screenShotImage, daguaiButton, "���"))
	    {
	      moveMouseTo(((int[])this.position.get("���"))[2], ((int[])this.position.get("���"))[3], this.mouseSpeed);
	      mouseClick();
	      return true;
	    }
	    BufferedImage tuichuButton = (BufferedImage)this.imageMaps.get("�˳�");
	    if (findPic(screenShotImage, tuichuButton, "�˳�"))
	    {
	      moveMouseTo(((int[])this.position.get("�˳�"))[2], ((int[])this.position.get("�˳�"))[3], this.mouseSpeed);
	      mouseClick();
	      return clickQueRenTuiChu(screenShotImage);
	    }
	    return false;
	  }
	  
	  private boolean clickQueRenTuiChu(BufferedImage screenShotImage)
	  {
	    BufferedImage querenButton = (BufferedImage)this.imageMaps.get("ȷ��");
	    if (findPic(screenShotImage, querenButton, "ȷ��"))
	    {
	      moveMouseTo(((int[])this.position.get("ȷ��"))[2], ((int[])this.position.get("ȷ��"))[3], this.mouseSpeed);
	      mouseClick();
	      return true;
	    }
	    return false;
	  }
	
	  private boolean clickTanSuo(BufferedImage screenShotImage)
	  {
	    BufferedImage tansuoButton = (BufferedImage)this.imageMaps.get("̽��");
	    if (findPic(screenShotImage, tansuoButton, "̽��"))
	    {
	      moveMouseTo(((int[])this.position.get("̽��"))[2], ((int[])this.position.get("̽��"))[3], this.mouseSpeed);
	      mouseClick();
	      return true;
	    }
	    return false;
	  }
	
	/**
	 * ʧ��
	 * @param screenShotImage
	 * @return
	 */
	  private boolean clickShiBai(BufferedImage screenShotImage)
	  {
	    BufferedImage shibai = (BufferedImage)this.imageMaps.get("ʧ��");
	    if (findPic(screenShotImage, shibai, "ʧ��"))
	    {
	      moveMouseTo(((int[])this.position.get("ʧ��"))[2], ((int[])this.position.get("ʧ��"))[3], this.mouseSpeed);
	      mouseClick(1, 3);
	      return true;
	    }
	    return false;
	  }
	
	
	/**
	 * �����ս��ť
	 * @param screenShotImage
	 */
	private boolean clickTiaoZhan(BufferedImage screenShotImage) {
		BufferedImage tiaozhanButton =  imageMaps.get("��ս");
		if( findPic(screenShotImage,tiaozhanButton,"��ս")){
			moveMouseTo(position.get("��ս")[2],position.get("��ս")[3],mouseSpeed);
			mouseClick();
			return true;
		}
		return false;
	}
	
	  private boolean clickJixuTiaoZhan(BufferedImage screenShotImage)
	  {
	    BufferedImage jixutiaozhan = (BufferedImage)this.imageMaps.get("����");
	    if (findPic(screenShotImage, jixutiaozhan, "����"))
	    {
	      moveMouseTo(((int[])this.position.get("����"))[2], ((int[])this.position.get("����"))[3], this.mouseSpeed);
	      mouseClick(1, 3);
	      return true;
	    }
	    return clickReturn(screenShotImage);
	  }
	  
	  private boolean clickReturn(BufferedImage screenShotImage)
	  {
	    BufferedImage fanhui = (BufferedImage)this.imageMaps.get("����");
	    if (findPic(screenShotImage, fanhui, "����"))
	    {
	      moveMouseTo(((int[])this.position.get("����"))[2], ((int[])this.position.get("����"))[3], this.mouseSpeed);
	      mouseClick();
	      System.out.println("---------�㵽��Ӱ�ť��-----------");
	      return true;
	    }
	    return false;
	  }
	

	private int getTotalMinus() {
		String hstr = hh.getText();
		String mstr = mm.getText();
		String sstr = ss.getText();
		if("".equals(hstr.trim()) || !hstr.trim().matches("^\\d+$")) {
			hstr = "00";
			hh.setText("00");
		}
		if("".equals(mstr.trim()) || !mstr.trim().matches("^\\d+$")) {
			mstr = "00";
			mm.setText("00");
		}
		if("".equals(sstr.trim()) || !sstr.trim().matches("^\\d+$")) {
			sstr = "00";
			ss.setText("00");
		}
		int h = Integer.parseInt(hstr) ;
		int m = Integer.parseInt(mstr) ;
		int s = Integer.parseInt(sstr) ;
		return h*60*60+m*60+s;
	}
	/**
	 * ģ�ⷢ��
	 */
	public void randomWait(){
		try{
			int a = getNum(0, 100);
			if(a > 98){
				Thread.sleep(3000);
			}else if(a > 90 && a <= 98 ){
				Thread.sleep(1000);
			}else if(a >80 && a < 90){
				Thread.sleep(600);
			}else if(a>60 && a< 80){
				Thread.sleep(getNum(100, 500));
			}else{
			}
		}catch(Exception e ){
			
		}
		
	}
	
	/**
	 * �����
	 */
	private void mouseClick() {
		try{
			robot.mousePress(InputEvent.BUTTON1_MASK);
			Thread.sleep(getNum(100, 300));
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			nopersonTimes = 0;
		}catch(Exception e ){
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * �����
	 */
	private void mouseClick(int min,int max) {
		int num = getNum(min, max);
		for(int i=0;i<num;i++){
			mouseClick();
			try{
			Thread.sleep(getNum(100, 300));
			}catch(Exception e ){
				System.out.println(e.getMessage());
			}
		}
	}
	
	
	//ȡ�����
	public int  getNum(int begin, int end){
		int res = new Random().nextInt(end-begin+1) + begin;
		return res;
	}
	
	//����ͼƬ
	private boolean findPic(BufferedImage screenShotImage,
			BufferedImage targetImage,String buttonName) {
		int [][] screenImageData = getImageGRB(screenShotImage);
		int [][] targetImageData = getImageGRB(targetImage);
		int targetImageHeight = targetImage.getHeight();
		int targetImageWidth = targetImage.getWidth();
		int screenImageHeight = screenShotImage.getHeight();
		int screenImageWidth = screenShotImage.getWidth();
		
		//�ȸ����ϴε�λ�ò��ң��Ҳ����ٱ�������
		int [] res = position.get(buttonName);
		if(res != null && res.length== 4){
			boolean isFinded = isMatchAll(res[1], res[0],targetImageHeight,targetImageWidth,screenImageHeight,screenImageWidth,screenImageData,targetImageData);
			if(isFinded) {
				int mouseX = res[0]+getNum(3, targetImageWidth);
				int mouseY = res[1]+getNum(3, targetImageHeight);
				position.put(buttonName, new int[]{res[0],res[1],mouseX,mouseY});
				return true;
			}
		}
		//������Ļ��ͼ���ص�����
		for(int y=0; y<screenImageHeight-targetImageHeight; y++) {
			for(int x=0; x<screenImageWidth-targetImageWidth; x++) {
				//����Ŀ��ͼ�ĳߴ磬�õ�Ŀ��ͼ�ĸ���ӳ�䵽��Ļ��ͼ�ϵ��ĸ��㣬
				//�жϽ�ͼ�϶�Ӧ���ĸ�����ͼB���ĸ������ص��ֵ�Ƿ���ͬ��
				//�����ͬ�ͽ���Ļ��ͼ��ӳ�䷶Χ�ڵ����еĵ���Ŀ��ͼ�����еĵ���бȽϡ�
				if(isSamePoint(targetImageData[0][0],screenImageData[y][x])
						&& isSamePoint(targetImageData[0][targetImageWidth-1],screenImageData[y][x+targetImageWidth-1])
						&& isSamePoint(targetImageData[targetImageHeight-1][targetImageWidth-1],screenImageData[y+targetImageHeight-1][x+targetImageWidth-1])
						&& isSamePoint(targetImageData[targetImageHeight-1][0],screenImageData[y+targetImageHeight-1][x])) {
					boolean isFinded = isMatchAll(y, x,targetImageHeight,targetImageWidth,screenImageHeight,screenImageWidth,screenImageData,targetImageData);
					//����ȽϽ����ȫ��ͬ����˵��ͼƬ�ҵ��������ҵ���λ���������ݵ����ҽ�����顣
					if(isFinded) {
						int mouseX = x+getNum(3, targetImageWidth);
						int mouseY = y+getNum(3, targetImageHeight);
						position.put(buttonName, new int[]{x,y,mouseX,mouseY});
						return true;
					}
				}
			}
		}
		return false;
	}

    /**
     * �ж���Ļ��ͼ��Ŀ��ͼӳ�䷶Χ�ڵ�ȫ�����Ƿ�ȫ����Сͼ�ĵ�һһ��Ӧ��
     * @param y - ��Ŀ��ͼ���Ͻ����ص���ƥ�����Ļ��ͼy����
     * @param x - ��Ŀ��ͼ���Ͻ����ص���ƥ�����Ļ��ͼx����
     * @return
     */
	private boolean isMatchAll(int y, int x,int keyImgHeight,int keyImgWidth,int scrShotImgHeight,int scrShotImgWidth,int [][] screenShotImageRGBData,int[][] keyImageRGBData) {
		int biggerY = 0;
		int biggerX = 0;
		boolean xor = false;
		int falsenum = 0;
		int totalfalse =(int)(keyImgHeight*keyImgWidth * 0.03) > 5 ? (int)(keyImgHeight*keyImgWidth * 0.03) : 5 ;
		for(int smallerY=0; smallerY<keyImgHeight; smallerY++) {
			biggerY = y+smallerY;
			for(int smallerX=0; smallerX<keyImgWidth; smallerX++) {
				biggerX = x+smallerX;
				xor = isSamePoint(keyImageRGBData[smallerY][smallerX],screenShotImageRGBData[biggerY][biggerX]);
				if(!xor) {
					falsenum ++;
					if(falsenum > totalfalse) return false;
				}
				
			}
			biggerX = x;
		}
		return true;
	}

	private boolean  isSamePoint(int a,int b){
		int coldiff =50 ;
		int rgb_a_r = (a & 0xff0000)>>16;
		int rgb_b_r = (b & 0xff0000)>>16;
		if(Math.abs(rgb_a_r - rgb_b_r)>coldiff) return false;
		int rgb_a_g =(a & 0xff00)>>8;
		int rgb_b_g =(b & 0xff00)>>8;
		if(Math.abs(rgb_a_g - rgb_b_g)>coldiff) return false;
		int rgb_a_b =(a & 0xff);
		int rgb_b_b =(b & 0xff);
		if(Math.abs(rgb_a_b - rgb_b_b)>coldiff) return false;
		
		return true;
	}
	
	public int[][] getImageGRB(BufferedImage bfImage) {
		int width = bfImage.getWidth();
		int height = bfImage.getHeight();
		int[][] result = new int[height][width];
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				//ʹ��getRGB(w, h)��ȡ�õ����ɫֵ��ARGB������ʵ��Ӧ����ʹ�õ���RGB��������Ҫ��ARGBת����RGB����bufImg.getRGB(w, h) & 0xFFFFFF��
				result[h][w] = bfImage.getRGB(w, h);// & 0xFFFFFF;
			}
		}
		return result;
		}
	
	/**
	 * ��ȡ��Ļ��ͼ
	 * @return
	 */
	private BufferedImage getScreenShot() {
		BufferedImage bfImage = null;
		int width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		try {
			Robot robot = new Robot();
			bfImage = robot.createScreenCapture(new Rectangle(0, 0, width, height));
		} catch (AWTException e) {
			e.printStackTrace();
		}
		return bfImage;
	}
	/**
	 * ��ȡͼƬ
	 * @param string
	 * @return
	 */
	private BufferedImage getImageFromPath(String Path) {
		BufferedImage bfImage = null;
		try {
			bfImage = ImageIO.read(new File(absolutePath+Path));
		} catch (Exception e) {
			e.printStackTrace();
			outputinfo.setText(outputinfo.getText()+"\\r\\n"+absolutePath+Path);
			System.out.println("·����"+absolutePath+Path+"�Ҳ����ļ�");
		}
		return bfImage;
		
	}
	/**
	 * ������Ļ
	 * @param beginx
	 * @param beginy
	 * @param endx
	 * @param endy
	 */
	public void slideScene(int beginx,int beginy,int endx,int endy){
		try{
		Thread.sleep(1000);
		}catch(Exception e){
			System.out.println(e.getStackTrace());
		}
		moveMouseTo(beginx, beginy, 10);
		robot.mousePress(InputEvent.BUTTON1_MASK);
		moveMouseTo(endx, endy, 10);
		robot.mouseRelease(InputEvent.BUTTON1_MASK);
	}
	

	/**
	 * �ƶ����
	 * @param x
	 * @param y
	 */
	public void moveMouseTo(int x , int y ,int mouseSpeed){
		if(robot != null){
			Point mousepoint = MouseInfo.getPointerInfo().getLocation();
			int _x = (int)mousepoint.getX();
			int _y = (int)mousepoint.getY();
			if(Math.abs(y-_y)<2 && Math.abs(x-_x) <2) return;
			try{
				while(true){
					Thread.sleep(10);
					if(x > _x && x > _x + mouseSpeed){ 
						_x= _x + mouseSpeed;
					}else if(x > _x && x <= _x + mouseSpeed){
						_x = x;
					}
					if(x < _x && x < _x - mouseSpeed){
						_x = _x- mouseSpeed;
					}else if(x < _x && x >= _x - mouseSpeed){
						_x = x;
					}
					if(y > _y && y > _y + mouseSpeed){ 
						_y= _y + mouseSpeed;
					}else if(y > _y && y <= _y + mouseSpeed){
						_y = y;
					}
					if(y < _y && y < _y - mouseSpeed){
						_y = _y- mouseSpeed;
					}else if(y < _y && y >= _y - mouseSpeed){
						_y = y;
					}
					robot.mouseMove(_x, _y);
					if(Math.abs(y-_y)<2 && Math.abs(x-_x) <2){
						break;
					}
				}
			}catch(Exception e){
				System.out.println(e.getMessage());
			}
		}
	}
	
	
	/**
	 * �����
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			//���ý�����
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			new Main();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
