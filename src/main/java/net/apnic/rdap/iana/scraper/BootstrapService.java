package net.apnic.rdap.iana.scraper;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

class BootstrapService
{
    public static final int NUMBER_OF_ELEMENTS = 2;
    public static final int RESOURCES_INDEX = 0;
    public static final int SERVERS_INDEX = 1;

    private List<URL> serversByURL = null;
    private List<List<String>> serviceElements = null;

    @JsonCreator
    public BootstrapService(List<List<String>> serviceElements)
    {
        if(serviceElements == null)
        {
            throw new IllegalArgumentException("serviceElements cannot be null");
        }
        else if(serviceElements.size() != NUMBER_OF_ELEMENTS)
        {
            throw new IllegalArgumentException("To few or to many elements in service");
        }

        this.serviceElements = serviceElements;
    }

    public List<String> getResources()
    {
        return serviceElements.get(RESOURCES_INDEX);
    }

    public List<String> getServers()
    {
        return serviceElements.get(SERVERS_INDEX);
    }

    public List<URL> getServersByURL()
        throws MalformedURLException
    {
        if(serversByURL == null)
        {
            serversByURL = new ArrayList<URL>();
            for(String server : getServers())
            {
                serversByURL.add(new URL(server));
            }
        }

        return serversByURL;
    }
}
