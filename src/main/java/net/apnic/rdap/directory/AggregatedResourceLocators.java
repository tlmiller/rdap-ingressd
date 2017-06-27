package net.apnic.rdap.directory;

import net.apnic.rdap.authority.RDAPAuthority;
import net.apnic.rdap.autnum.AsnRange;
import net.apnic.rdap.domain.Domain;
import net.apnic.rdap.entity.Entity;
import net.apnic.rdap.nameserver.NameServer;
import net.apnic.rdap.resource.ResourceLocator;
import net.apnic.rdap.resource.ResourceNotFoundException;
import net.ripe.ipresource.IpRange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AggregatedResourceLocators implements ResourceLocator<Object> {

    private ResourceLocator<AsnRange> asnLocator;
    private ResourceLocator<Domain> domainLocator;
    private ResourceLocator<Entity> entityLocator;
    private ResourceLocator<IpRange> ipLocator;
    private ResourceLocator<NameServer> nsLocator;

    @Autowired
    public AggregatedResourceLocators(ResourceLocator<AsnRange> asnLocator,
                                      ResourceLocator<Domain> domainLocator,
                                      ResourceLocator<Entity> entityLocator,
                                      ResourceLocator<IpRange> ipLocator,
                                      ResourceLocator<NameServer> nsLocator)
    {
        this.asnLocator = asnLocator;
        this.domainLocator = domainLocator;
        this.entityLocator = entityLocator;
        this.ipLocator = ipLocator;
        this.nsLocator = nsLocator;
    }

    @Override
    public RDAPAuthority authorityForResource(Object resource) throws ResourceNotFoundException {
        if(resource instanceof AsnRange) {
            return asnLocator.authorityForResource((AsnRange)resource);
        }
        else if(resource instanceof NameServer) {
            return nsLocator.authorityForResource((NameServer)resource);
        }
        else if(resource instanceof Domain) {
            return domainLocator.authorityForResource((Domain)resource);
        }
        else if(resource instanceof Entity) {
            return entityLocator.authorityForResource((Entity)resource);
        }
        else if(resource instanceof IpRange) {
            return ipLocator.authorityForResource((IpRange)resource);
        }
        else {
            throw new IllegalStateException("Can't find authority for resource " + resource);
        }
    }
}
