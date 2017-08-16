package com.mingrisoft;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.log4j.Logger;

public class ExceptionHandler implements UncaughtExceptionHandler{
	public static Logger logger = Logger.getLogger(MyServerHandler.class);

    @Override
    public void uncaughtException(Thread t, Throwable e) {
    	logger.info("handle run in thread:"+Thread.currentThread().getName());

    	logger.info("thread:"+t.getName()+" throwing exception");
          
    	logger.info(e.toString());
    }

}
