package ieee1516e;

import java.util.Random;

public class Samochodjeden {

	public boolean czymyjnia;
	public boolean wtrakcie;
	public int nrstanowiska;
	public int nrsamochodu;
	public float pojemnosc;
	
	Samochodjeden(int nrsamochodu){
		this.nrsamochodu = nrsamochodu;
		this.nrstanowiska = 0;
		this.wtrakcie = false;
		this.czymyjnia = new Random().nextBoolean();
	}
}
	
	