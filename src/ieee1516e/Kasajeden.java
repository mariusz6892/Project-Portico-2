package ieee1516e;

public class Kasajeden {

	public boolean czywolny;
	public static double zaplanowanyczas = 10.0;
	public double aktualnyczas;
	public int obslugiwanysamochod;
	
	Kasajeden(){
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
	
	public void placenie() {
		aktualnyczas++;
	}
	
	public void zajmij(){
		czywolny = false;
	}
	
	public boolean styknie() {
		if(zaplanowanyczas == aktualnyczas) return true;
		else return false;
	}
	
	public void koniecplacenia() {
		czywolny = true;
		aktualnyczas = 0.0;
	}
}
