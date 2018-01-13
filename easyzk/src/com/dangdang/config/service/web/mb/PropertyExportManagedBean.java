package com.dangdang.config.service.web.mb;

import com.dangdang.config.business.INodeBusiness;
import com.dangdang.config.service.INodeService;
import com.dangdang.config.service.entity.PropertyItemVO;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import org.apache.commons.io.IOUtils;
import org.apache.curator.utils.ZKPaths;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBean(name="propertyExportMB")
@RequestScoped
public class PropertyExportManagedBean
{

  @ManagedProperty("#{nodeService}")
  private INodeService nodeService;

  @ManagedProperty("#{nodeBusiness}")
  private INodeBusiness nodeBusiness;

  @ManagedProperty("#{nodeAuthMB}")
  private NodeAuthManagedBean nodeAuth;

  @ManagedProperty("#{versionMB}")
  private VersionManagedBean versionMB;
  private static final Logger LOGGER = LoggerFactory.getLogger(PropertyExportManagedBean.class);

  public void setNodeService(INodeService nodeService)
  {
    this.nodeService = nodeService;
  }

  public void setNodeBusiness(INodeBusiness nodeBusiness)
  {
    this.nodeBusiness = nodeBusiness;
  }

  public void setNodeAuth(NodeAuthManagedBean nodeAuth)
  {
    this.nodeAuth = nodeAuth;
  }

  public void setVersionMB(VersionManagedBean versionMB)
  {
    this.versionMB = versionMB;
  }

  public StreamedContent generateFile(String groupName)
  {
    LOGGER.info("Export config group: {}", groupName);

    StreamedContent file = null;
    if (!Strings.isNullOrEmpty(groupName)) {
      List items = this.nodeBusiness.findPropertyItems(this.nodeAuth.getAuthedNode(), this.versionMB.getSelectedVersion(), groupName);

      if (!items.isEmpty()) {
        ByteArrayOutputStream out = null;
        try {
          out = new ByteArrayOutputStream();
          List lines = formatPropertyLines(groupName, items);
          IOUtils.writeLines(lines, "\r\n", out, Charsets.UTF_8.displayName());
          InputStream in = new ByteArrayInputStream(out.toByteArray());

          String fileName = groupName + ".properties";
          file = new DefaultStreamedContent(in, "text/plain", fileName, Charsets.UTF_8.name());
        } catch (IOException e) {
          LOGGER.error(e.getMessage(), e);
        } finally {
          if (out != null)
            try {
              out.close();
            }
            catch (IOException e)
            {
            }
        }
      }
    }
    return file;
  }

  private List<String> formatPropertyLines(String groupName, List<PropertyItemVO> items) {
    List lines = Lists.newArrayList();
    lines.add(String.format("# Export from zookeeper configuration group: [%s] - [%s] - [%s].", new Object[] { this.nodeAuth.getAuthedNode(), this.versionMB.getSelectedVersion(), groupName }));

    for (PropertyItemVO item : items) {
      if (!Strings.isNullOrEmpty(item.getComment())) {
        lines.add("# " + item.getComment());
      }
      lines.add(item.getName() + "=" + item.getValue());
    }
    return lines;
  }

  public StreamedContent generateFileAll()
  {
    LOGGER.info("Export all config group");
    StreamedContent file = null;

    String authedNode = ZKPaths.makePath(this.nodeAuth.getAuthedNode(), this.versionMB.getSelectedVersion());
    List<String> children = this.nodeService.listChildren(authedNode);
    if ((children != null) && (!children.isEmpty())) {
      try {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(out);
        for (String groupName : children) {
          String groupPath = ZKPaths.makePath(authedNode, groupName);
          String fileName = ZKPaths.getNodeFromPath(groupPath) + ".properties";

          List items = this.nodeBusiness.findPropertyItems(this.nodeAuth.getAuthedNode(), this.versionMB.getSelectedVersion(), groupName);
          List lines = formatPropertyLines(groupName, items);
          if (!lines.isEmpty()) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOutputStream.putNextEntry(zipEntry);
            IOUtils.writeLines(lines, "\r\n", zipOutputStream, Charsets.UTF_8.displayName());
            zipOutputStream.closeEntry();
          }
        }

        zipOutputStream.close();
        byte[] data = out.toByteArray();
        InputStream in = new ByteArrayInputStream(data);

        String fileName = authedNode.replace('/', '-') + ".zip";
        file = new DefaultStreamedContent(in, "application/zip", fileName, Charsets.UTF_8.name());
      } catch (IOException e) {
        LOGGER.error(e.getMessage(), e);
      }
    }

    return file;
  }
}