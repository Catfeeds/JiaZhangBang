package com.runcom.jiazhangbang.listenWrite;

public class ListenWriteGameItemBean
{
	private String phrase , voice , pinyin;

	public String getPhrase()
	{
		return phrase;
	}

	public void setPhrase(String phrase )
	{
		this.phrase = phrase;
	}

	public String getVoice()
	{
		return voice;
	}

	public void setVoice(String voice )
	{
		this.voice = voice;
	}

	public String getPinyin()
	{
		return pinyin;
	}

	public void setPinyin(String pinyin )
	{
		this.pinyin = pinyin;
	}

	@Override
    public String toString()
    {
	    return "ListenWriteGameItemBean [phrase=" + phrase + ", voice=" + voice + ", pinyin=" + pinyin + "]";
    }
	
}
