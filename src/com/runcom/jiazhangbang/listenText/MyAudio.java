/**
 * 
 */
package com.runcom.jiazhangbang.listenText;

/**
 * @author Administrator
 * @copyright wgcwgc
 * @date 2017-4-19
 * @time ÏÂÎç4:11:26
 * @project_name JiaZhangBang
 * @package_name com.runcom.jiazhangbang.listenText
 * @file_name MyAudio.java
 * @type_name MyAudio
 * @enclosing_type
 * @tags
 * @todo
 * @others
 * 
 */

public class MyAudio
{
	private String name , source , lyric;
	private int id;

	/**
	 * @return the id
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id )
	{
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name )
	{
		this.name = name;
	}

	/**
	 * @return the source
	 */
	public String getSource()
	{
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(String source )
	{
		this.source = source;
	}

	/**
	 * @return the lyric
	 */
	public String getLyric()
	{
		return lyric;
	}

	/**
	 * @param lyric
	 *            the lyric to set
	 */
	public void setLyric(String lyric )
	{
		this.lyric = lyric;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "MyAudio [id=" + id + ", name=" + name + ", source=" + source + ", lyric=" + lyric + "]";
	}

}
