package com.runcom.jiazhangbang.listenWrite;

import android.media.Image;

public class NewWords
{
	private int id;
	private String name , other;
	private Image image;

	public int getId()
	{
		return id;
	}

	public void setId(int id )
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name )
	{
		this.name = name;
	}

	public String getOther()
	{
		return other;
	}

	public void setOther(String other )
	{
		this.other = other;
	}

	public Image getImage()
	{
		return image;
	}

	public void setImage(Image image )
	{
		this.image = image;
	}

	@Override
    public String toString()
    {
	    return "NewWords [id=" + id + ", name=" + name + ", other=" + other + ", image=" + image + "]";
    }

}
