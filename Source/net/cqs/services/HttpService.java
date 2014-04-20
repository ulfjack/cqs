package net.cqs.services;

import javax.servlet.Filter;
import javax.servlet.Servlet;

import de.ofahrt.catfish.RequestListener;

public interface HttpService extends Service
{

void registerServlet(String pathSpec, String fileSpec, Servlet servlet);
void registerFilter(String pathSpec, String fileSpec, Filter filter);

void registerRequestListener(RequestListener listener);

}
