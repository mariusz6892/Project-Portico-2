package ieee1516e;

public class Dystrybutorjeden {

	public boolean czywolny;
	public boolean dostalem;
	public int nrstanowiska;
	public double zaplanowanyczas;
	public double aktualnyczas;
	public int obslugiwanysamochod;
	
	Dystrybutorjeden(int nrstanowiska){
		this.czywolny = true;
		this.dostalem = false;
		this.nrstanowiska = nrstanowiska;
		this.zaplanowanyczas = 10.0;
	}
	
	public boolean getczywolny() {
		return czywolny;
	}
	
	public boolean getdostalem() {
		return dostalem;
	}
	
	public double getzaplanowanyczas() {
		return zaplanowanyczas;
	}
	
	public double getaktualnyczas() {
		return aktualnyczas;
	}
	
	public void tankowanie() {
		aktualnyczas++;
	}
	
	public void zajmij(){
		czywolny = false;
	}
	
	public void dostalem() {
		dostalem = true;
	}
	
	public void ilelac(double pojemnosc) {
		zaplanowanyczas = pojemnosc;
	}
	
	public boolean styknie() {
		if(zaplanowanyczas == aktualnyczas) return true;
		else return false;
	}
	
	public void koniectankowania() {
		czywolny = true;
		dostalem = false;
		zaplanowanyczas = 0.0;
		aktualnyczas = 0.0;
	}
}
