package ch.mno.tatoo.facade.tac;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by dutoitc on 17/08/15.
 * c.f. https://help.talend.com/reader/XKiHN_uvNCRjt1~QcNn4fw/URmRoWm7YAuFC06smWxO9g
 */
public class MetaservletFacade {

	private Logger LOG = LoggerFactory.getLogger(MetaservletFacade.class);
	private String tacUrl;
	private Base64 b64 = new Base64(true);

	public MetaservletFacade(String tacUrl) {
		this.tacUrl = tacUrl;
	}

	public String call(String json) throws URISyntaxException, IOException {
		String encode = b64.encodeToString(json.getBytes()).replace("\n", "").replace("\r","");
		String url = tacUrl + "/metaServlet?" + encode;

		// Remote call and JSON Parse
		LOG.trace("Calling " + url);
		String resultStr = IOUtils.toString(new URI(url));

		return resultStr;
	}


}
