package ieee1516e;

public class Myjniajeden {

	public boolean czywolny;
	public static double zaplanowanyczas = 60.0;
	public double aktualnyczas;
	public int obslugiwanysamochod;
	
	Myjniajeden(){
		this.czywolny = true;
	}
	
	public boolean getczywolny() {
		return czywolny;
	}
	
	public double getzaplanowanyczas() {
		return zaplanowanyczas;
	}
	
	public double getaktualnyczas() {
		return aktualnyczas;
	}
	
	public void mycie() {
		aktualnyczas++;
	}
	
	public void zajmij(){
		czywolny = false;
	}
	
	public boolean styknie() {
		if(zaplanowanyczas == aktualnyczas) return true;
		else return false;
	}
	
	public void koniecmycia() {
		czywolny = true;
		aktualnyczas = 0.0;
	}
}
