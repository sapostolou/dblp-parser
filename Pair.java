package dblpParser;

public class Pair{
	private String conf;
	private int freq;
	public Pair(String conf,int freq){
		this.conf = conf;
		this.freq = freq;
	}

	public String getConf(){
		return conf;
	}

	public Integer getFreq(){
		return freq;
	}
}