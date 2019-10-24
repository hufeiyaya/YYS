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
	 * 版本号
	 */
	private static final long serialVersionUID = -135885139973224087L;
	private String shuoming = "1.大小默认为官方桌面版打开"
			+ "\r\n2.目前仅支持单人刷本。"
			+ "\r\n4.倒计时初始为0则不停止。"
			+ "\r\n5.以管理员身份运行，否则无权调用鼠标！"
			+ "\r\n               author:   hf"
			+ "\r\n               date  :    2019年7月1日";
	
	JButton startOrPause = new JButton();//开始暂停按钮
	JLabel actionType = new JLabel("战斗类型："); 
	JComboBox<String> act = new JComboBox<String>(new String[]{"御魂&觉醒","探索","御灵","业原火"});
	JLabel yaoqing = new JLabel("接受邀请："); 
	JComboBox<String> yq = new JComboBox<String>(new String[]{"否","是"});
	JLabel chaoguiwang = new JLabel("超鬼王："); 
	JComboBox<String> cgw = new JComboBox<String>(new String[]{"否","是"});
	JLabel fenbianlv = new JLabel("分辨率：");
	JComboBox<String> fbl = new JComboBox<String>(new String[]{"1280*720","960*540"});
	JLabel daojishi = new JLabel("倒计时："); 
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
	//创建循环线程
	Thread t = new Thread(){
		public void run() {
			while(true){
				try {
					Thread.sleep(getNum(200, 400));
					if(isstart){
						doPlayAction();//开始刷图
					}
				} catch (Exception e) {
					e.printStackTrace();
					//TODO 输出错误信息。
					outputinfo.setText(e.getMessage());
					doPause();//暂停刷图
				}
			}
		};
	};
	
	//创建倒计时线程
	Thread timemanager = new Thread(){
		public void run() {
			while(true){
				try {
					Thread.sleep(1000);
					doTimeCountDown();
				} catch (Exception e) {
					//TODO 输出错误信息。
					outputinfo.setText(e.getMessage());
				}
			}
		}

	};
	
	//创建脚本面板：开始，暂停，刷御魂，刷觉醒。
	Main(){
		super();
		Color cc =new Color((int) (Math.random()*255),(int) (Math.random()*255),(int) (Math.random()*255));
		SwingUtilities.updateComponentTreeUI(getContentPane()); 
		setSize(200, 500);//设置宽高
		setLocationRelativeTo(null);//设置显示位置
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//设置关闭动作
		setVisible(true);//显示
		setResizable(false);//设置是否可以调整大小
		setLayout(null);//设置布局。
		setComponent();//设置组件，添加事件。
		addComponent();//添加按钮、及选择项
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
		//setAlwaysOnTop(true);//面板始终在屏幕最前方
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
		//添加键盘事件。
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
		    public void eventDispatched(AWTEvent event) {
		        if (((KeyEvent) event).getID() == KeyEvent.KEY_RELEASED) {
		        	if(((KeyEvent) event).getKeyCode() == 32){//按空格键 开始/暂停
		        		startOrPause.doClick(30);
		        	}
		        }
		    }
		}, AWTEvent.KEY_EVENT_MASK);
		outputinfo.setText(shuoming);
	}
	//计算显示时分秒
	private void doTimeCountDown() {
		if(isstart && totalMinus>0 ){//时间大于0,则减一
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
	 * 获取keyimages
	 * @return
	 */
	private Map<String, BufferedImage> getImages(String fblpath) {
		//读取按钮图片。
		Map<String,BufferedImage> imagesMap = new HashMap<String,BufferedImage>();
		imagesMap.put("胜利", getImageFromPath("/images/"+fblpath+"/shengli.png"));
		imagesMap.put("失败", getImageFromPath("/images/"+fblpath+"/shibai.png"));
		imagesMap.put("挑战", getImageFromPath("/images/"+fblpath+"/tiaozhan.png"));
		imagesMap.put("继续", getImageFromPath("/images/" + fblpath + "/zhanlipin.png"));
		imagesMap.put("探索", getImageFromPath("/images/" + fblpath + "/tansuo.png"));
	    imagesMap.put("打怪", getImageFromPath("/images/" + fblpath + "/daguai.png"));
	    imagesMap.put("退出", getImageFromPath("/images/" + fblpath + "/tuichu.png"));
	    imagesMap.put("确认", getImageFromPath("/images/" + fblpath + "/queren.png"));
	    imagesMap.put("返回", getImageFromPath("/images/" + fblpath + "/fanhui.png"));
		imagesMap.put("金币", getImageFromPath("/images/"+fblpath+"/jinbi.png"));
		return imagesMap;
	}

	//初始化面板组件
	private void setComponent() {
		//设置按钮名称
		startOrPause.setText("开始");
		startOrPause.setPreferredSize(new Dimension(100, 50));//设置按钮大小
		startOrPause.setBounds(25, 10, 150, 50);
		startOrPause.setFont(new Font("宋体", Font.BOLD,25));
		startOrPause.addActionListener(this);
		//分辨率
		fenbianlv.setBounds(5, 73, 65, 30);
		fenbianlv.setFont(new Font("宋体", Font.BOLD,15));
		fbl.setBounds(80, 70, 100, 30);
		fbl.setFont(new Font("宋体", Font.BOLD,13));
		fbl.addActionListener(this);
		//接受邀请
		yaoqing.setBounds(5, 113, 80, 30);
		yaoqing.setFont(new Font("宋体", Font.BOLD,15));
		yq.setBounds(80, 110, 100, 30);
		yq.setFont(new Font("宋体", Font.BOLD,15));
		
		//超鬼王
		chaoguiwang.setBounds(5, 153, 80, 30);
		chaoguiwang.setFont(new Font("宋体", Font.BOLD,15));
		cgw.setBounds(80, 150, 100, 30);
		cgw.setFont(new Font("宋体", Font.BOLD,15));
		
		//战斗类型
		actionType.setBounds(5, 190, 80, 30);
		actionType.setFont(new Font("宋体", Font.BOLD,15));
		act.setBounds(80, 187, 108, 30);
		act.setFont(new Font("宋体", Font.BOLD,15));

		//倒计时
		daojishi.setBounds(5, 223, 85, 30);
		daojishi.setFont(new Font("宋体", Font.BOLD,15));
		hh.setBounds(80, 223, 30, 30);
		hh.setFont(new Font("宋体", Font.BOLD,15));
		maohao1.setBounds(110, 223, 10, 30);
		maohao1.setFont(new Font("宋体", Font.BOLD,15));
		mm.setBounds(120, 223, 30, 30);
		mm.setFont(new Font("宋体", Font.BOLD,15));
		maohao2.setBounds(150, 223, 10, 30);
		maohao2.setFont(new Font("宋体", Font.BOLD,15));
		ss.setBounds(160, 223, 30, 30);
		ss.setFont(new Font("宋体", Font.BOLD,15));
		ss.setEditable(false);
		//输出信息
		outputinfo.setBounds(5, 275, 185, 170);
		outputinfo.setEditable(true);
		outputinfo.setOpaque(false);
		outputinfo.setLineWrap(true);
	}
	//添加组件至面板
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


	//事件监听
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == startOrPause){
			if("开始".equals(startOrPause.getText())){//未启动
				doStart();
			}else if("暂停".equals(startOrPause.getText())){
				doPause();
			}
		}else if(e.getSource() == fbl){
			if("960*540".equals(fbl.getSelectedItem())){
				imageMaps = imagesMap.get("960*540");
				outputinfo.setText("切换到分辨率：960*540");
			}else if("1280*720".equals(fbl.getSelectedItem())){
				imageMaps = imagesMap.get("1280*720");
				outputinfo.setText("切换到分辨率：1280*720");
			}
		}
		
	}
	
	/**
	 * 启动刷图循环。
	 */
	public void doStart(){
		isstart = true;
		nopersonTimes = 0;
		startOrPause.setText("暂停");
		outputinfo.setText("开始了哦！");
		fbl.setEnabled(false);
		hh.setEditable(false);
		mm.setEditable(false);
		totalMinus = getTotalMinus();
	}


	/**
	 * 停止刷图循环。
	 */
	public void doPause(){
		isstart = false;
		nopersonTimes = 0;
		startOrPause.setText("开始");
		fbl.setEnabled(true);
		hh.setEditable(true);
		mm.setEditable(true);
		outputinfo.setText(shuoming);
	}

	/**
	 * 开始刷图
	 */
	private void doPlayAction(){
		//截屏
		BufferedImage screenShotImage = getScreenShot();
		if(screenShotImage == null || imageMaps == null) return;
		if("御魂&觉醒".equals(act.getSelectedItem()))
		{
			if(clickTiaoZhan(screenShotImage)) return;//开始挑战
			if(clickJixuTiaoZhan(screenShotImage)) return;//继续挑战
			if(clickShiBai(screenShotImage)) return;//失败
		}
		else if("探索".equals(act.getSelectedItem()))
		{
			if(clickTanSuo(screenShotImage)) return;//开始挑战
			if(clickDaGuai(screenShotImage)) return;//胜利图片
			if(clickQueRenTuiChu(screenShotImage)) return;//失败图片
			if(clickJixuTiaoZhan(screenShotImage)) return;//胜利图片
			if(clickShiBai(screenShotImage)) return;//失败
		}
	}
	
	  private boolean clickDaGuai(BufferedImage screenShotImage)
	  {
	    BufferedImage daguaiButton = (BufferedImage)this.imageMaps.get("打怪");
	    if (findPic(screenShotImage, daguaiButton, "打怪"))
	    {
	      moveMouseTo(((int[])this.position.get("打怪"))[2], ((int[])this.position.get("打怪"))[3], this.mouseSpeed);
	      mouseClick();
	      return true;
	    }
	    BufferedImage tuichuButton = (BufferedImage)this.imageMaps.get("退出");
	    if (findPic(screenShotImage, tuichuButton, "退出"))
	    {
	      moveMouseTo(((int[])this.position.get("退出"))[2], ((int[])this.position.get("退出"))[3], this.mouseSpeed);
	      mouseClick();
	      return clickQueRenTuiChu(screenShotImage);
	    }
	    return false;
	  }
	  
	  private boolean clickQueRenTuiChu(BufferedImage screenShotImage)
	  {
	    BufferedImage querenButton = (BufferedImage)this.imageMaps.get("确认");
	    if (findPic(screenShotImage, querenButton, "确认"))
	    {
	      moveMouseTo(((int[])this.position.get("确认"))[2], ((int[])this.position.get("确认"))[3], this.mouseSpeed);
	      mouseClick();
	      return true;
	    }
	    return false;
	  }
	
	  private boolean clickTanSuo(BufferedImage screenShotImage)
	  {
	    BufferedImage tansuoButton = (BufferedImage)this.imageMaps.get("探索");
	    if (findPic(screenShotImage, tansuoButton, "探索"))
	    {
	      moveMouseTo(((int[])this.position.get("探索"))[2], ((int[])this.position.get("探索"))[3], this.mouseSpeed);
	      mouseClick();
	      return true;
	    }
	    return false;
	  }
	
	/**
	 * 失败
	 * @param screenShotImage
	 * @return
	 */
	  private boolean clickShiBai(BufferedImage screenShotImage)
	  {
	    BufferedImage shibai = (BufferedImage)this.imageMaps.get("失败");
	    if (findPic(screenShotImage, shibai, "失败"))
	    {
	      moveMouseTo(((int[])this.position.get("失败"))[2], ((int[])this.position.get("失败"))[3], this.mouseSpeed);
	      mouseClick(1, 3);
	      return true;
	    }
	    return false;
	  }
	
	
	/**
	 * 点击挑战按钮
	 * @param screenShotImage
	 */
	private boolean clickTiaoZhan(BufferedImage screenShotImage) {
		BufferedImage tiaozhanButton =  imageMaps.get("挑战");
		if( findPic(screenShotImage,tiaozhanButton,"挑战")){
			moveMouseTo(position.get("挑战")[2],position.get("挑战")[3],mouseSpeed);
			mouseClick();
			return true;
		}
		return false;
	}
	
	  private boolean clickJixuTiaoZhan(BufferedImage screenShotImage)
	  {
	    BufferedImage jixutiaozhan = (BufferedImage)this.imageMaps.get("继续");
	    if (findPic(screenShotImage, jixutiaozhan, "继续"))
	    {
	      moveMouseTo(((int[])this.position.get("继续"))[2], ((int[])this.position.get("继续"))[3], this.mouseSpeed);
	      mouseClick(1, 3);
	      return true;
	    }
	    return clickReturn(screenShotImage);
	  }
	  
	  private boolean clickReturn(BufferedImage screenShotImage)
	  {
	    BufferedImage fanhui = (BufferedImage)this.imageMaps.get("返回");
	    if (findPic(screenShotImage, fanhui, "返回"))
	    {
	      moveMouseTo(((int[])this.position.get("返回"))[2], ((int[])this.position.get("返回"))[3], this.mouseSpeed);
	      mouseClick();
	      System.out.println("---------点到组队按钮了-----------");
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
	 * 模拟发呆
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
	 * 鼠标点击
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
	 * 鼠标点击
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
	
	
	//取随机数
	public int  getNum(int begin, int end){
		int res = new Random().nextInt(end-begin+1) + begin;
		return res;
	}
	
	//查找图片
	private boolean findPic(BufferedImage screenShotImage,
			BufferedImage targetImage,String buttonName) {
		int [][] screenImageData = getImageGRB(screenShotImage);
		int [][] targetImageData = getImageGRB(targetImage);
		int targetImageHeight = targetImage.getHeight();
		int targetImageWidth = targetImage.getWidth();
		int screenImageHeight = screenShotImage.getHeight();
		int screenImageWidth = screenShotImage.getWidth();
		
		//先根据上次的位置查找，找不到再遍历查找
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
		//遍历屏幕截图像素点数据
		for(int y=0; y<screenImageHeight-targetImageHeight; y++) {
			for(int x=0; x<screenImageWidth-targetImageWidth; x++) {
				//根据目标图的尺寸，得到目标图四个角映射到屏幕截图上的四个点，
				//判断截图上对应的四个点与图B的四个角像素点的值是否相同，
				//如果相同就将屏幕截图上映射范围内的所有的点与目标图的所有的点进行比较。
				if(isSamePoint(targetImageData[0][0],screenImageData[y][x])
						&& isSamePoint(targetImageData[0][targetImageWidth-1],screenImageData[y][x+targetImageWidth-1])
						&& isSamePoint(targetImageData[targetImageHeight-1][targetImageWidth-1],screenImageData[y+targetImageHeight-1][x+targetImageWidth-1])
						&& isSamePoint(targetImageData[targetImageHeight-1][0],screenImageData[y+targetImageHeight-1][x])) {
					boolean isFinded = isMatchAll(y, x,targetImageHeight,targetImageWidth,screenImageHeight,screenImageWidth,screenImageData,targetImageData);
					//如果比较结果完全相同，则说明图片找到，填充查找到的位置坐标数据到查找结果数组。
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
     * 判断屏幕截图上目标图映射范围内的全部点是否全部和小图的点一一对应。
     * @param y - 与目标图左上角像素点想匹配的屏幕截图y坐标
     * @param x - 与目标图左上角像素点想匹配的屏幕截图x坐标
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
				//使用getRGB(w, h)获取该点的颜色值是ARGB，而在实际应用中使用的是RGB，所以需要将ARGB转化成RGB，即bufImg.getRGB(w, h) & 0xFFFFFF。
				result[h][w] = bfImage.getRGB(w, h);// & 0xFFFFFF;
			}
		}
		return result;
		}
	
	/**
	 * 获取屏幕截图
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
	 * 获取图片
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
			System.out.println("路径："+absolutePath+Path+"找不到文件");
		}
		return bfImage;
		
	}
	/**
	 * 滑动屏幕
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
	 * 移动鼠标
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
	 * 主入口
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			//设置界面风格
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			new Main();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
