package com.dangdang.config.service.web.mb;

import com.dangdang.config.service.INodeService;
import com.dangdang.config.service.entity.PropertyItemVO;
import com.dangdang.config.service.observer.IObserver;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.apache.commons.io.IOUtils;
import org.apache.curator.utils.ZKPaths;
import org.primefaces.component.inputtext.InputText;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

@ManagedBean(name="propertyGroupMB")
@ViewScoped
public class PropertyGroupManagedBean
  implements Serializable, IObserver
{
  private static final long serialVersionUID = 1L;

  @ManagedProperty("#{nodeService}")
  private INodeService nodeService;

  @ManagedProperty("#{nodeAuthMB}")
  private NodeAuthManagedBean nodeAuth;

  @ManagedProperty("#{nodeDataMB}")
  private NodeDataManagedBean nodeData;

  @ManagedProperty("#{versionMB}")
  private VersionManagedBean versionMB;
  private static final Logger LOGGER = LoggerFactory.getLogger(PropertyGroupManagedBean.class);
  private List<String> propertyGroups;
  private String selectedGroup;
  private InputText newPropertyGroup;
  private Splitter PROPERTY_SPLITTER = Splitter.on('=').limit(2);

  public void setNodeService(INodeService nodeService)
  {
    this.nodeService = nodeService;
  }

  public void setNodeAuth(NodeAuthManagedBean nodeAuth)
  {
    this.nodeAuth = nodeAuth;
  }

  public final void setNodeData(NodeDataManagedBean nodeData)
  {
    this.nodeData = nodeData;
  }

  public void setVersionMB(VersionManagedBean versionMB)
  {
    this.versionMB = versionMB;
  }

  public List<String> getPropertyGroups()
  {
    return this.propertyGroups;
  }

  public String getSelectedGroup()
  {
    return this.selectedGroup;
  }

  public void setSelectedGroup(String selectedGroup) {
    this.selectedGroup = selectedGroup;
  }

  @PostConstruct
  private void init() {
    this.nodeAuth.register(this);
    refreshGroup();
  }

  public InputText getNewPropertyGroup()
  {
    return this.newPropertyGroup;
  }

  public void setNewPropertyGroup(InputText newPropertyGroup) {
    this.newPropertyGroup = newPropertyGroup;
  }

  public void createNode()
  {
    String newPropertyGroupName = (String)this.newPropertyGroup.getValue();
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Create new node: {}", newPropertyGroupName);
    }
    String authedNode = ZKPaths.makePath(this.nodeAuth.getAuthedNode(), this.versionMB.getSelectedVersion());
    boolean created = this.nodeService.createProperty(ZKPaths.makePath(authedNode, newPropertyGroupName), null);
    if (created) {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Property group created.", newPropertyGroupName));
      refreshGroup();
      this.newPropertyGroup.setValue(null);
      this.nodeData.refreshNodeProperties(null);
    } else {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Property group creation failed.", newPropertyGroupName));
    }
  }

  public void deleteNode(String propertyGroup)
  {
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Delete node [{}] for property group.", propertyGroup);
    }

    String versionedRoot = ZKPaths.makePath(this.nodeAuth.getAuthedNode(), this.versionMB.getSelectedVersion());
    String versionedRootComment = ZKPaths.makePath(this.nodeAuth.getAuthedNode(), this.versionMB.getSelectedVersion() + "$");

    this.nodeService.deleteProperty(ZKPaths.makePath(versionedRoot, propertyGroup));
    this.nodeService.deleteProperty(ZKPaths.makePath(versionedRootComment, propertyGroup));

    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Property group deleted.", propertyGroup));
    refreshGroup();
  }

  public void onMenuSelected(SelectEvent event)
  {
    String selectedNode = (String)event.getObject();

    LOGGER.info("Tree item changed to {}.", selectedNode);

    this.nodeData.refreshNodeProperties(selectedNode);
  }

  public void propertyGroupUpload(FileUploadEvent event)
  {
    String fileName = event.getFile().getFileName();
    LOGGER.info("Deal uploaded file: {}", fileName);
    String group = Files.getNameWithoutExtension(fileName);
    InputStream inputstream = null;
    try {
      inputstream = event.getFile().getInputstream();
      savePropertyGroup(fileName, group, inputstream);
    } catch (IOException e) {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "File parse error.", fileName));
      LOGGER.error("Upload File Exception.", e);
    } finally {
      if (inputstream != null)
        try {
          inputstream.close();
        }
        catch (IOException e)
        {
        }
    }
  }

  private void savePropertyGroup(String fileName, String group, InputStream inputstream) throws IOException {
    List<PropertyItemVO> items = parseInputFile(inputstream);
    if (!items.isEmpty()) {
      String groupFullPath = ZKPaths.makePath(ZKPaths.makePath(this.nodeAuth.getAuthedNode(), this.versionMB.getSelectedVersion()), group);
      String commentFullPath = ZKPaths.makePath(ZKPaths.makePath(this.nodeAuth.getAuthedNode(), this.versionMB.getSelectedVersion() + "$"), group);

      boolean created = this.nodeService.createProperty(groupFullPath, null);
      if (created) {
        for (PropertyItemVO item : items) {
          this.nodeService.createProperty(ZKPaths.makePath(groupFullPath, item.getName()), item.getValue());
          if (!Strings.isNullOrEmpty(item.getComment())) {
            this.nodeService.createProperty(ZKPaths.makePath(commentFullPath, item.getName()), item.getComment());
          }
        }
        refreshGroup();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Succesful", fileName + " is uploaded."));
      } else {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Create group with file error.", fileName));
      }
    }
    else {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "File is empty.", fileName));
    }
  }

  private List<PropertyItemVO> parseInputFile(InputStream inputstream)
    throws IOException
  {
    List lines = IOUtils.readLines(inputstream, Charsets.UTF_8.name());
    List items = Lists.newArrayList();
    String previousLine = null;
    for (int i = 1; i < lines.size(); i++) {
      String line = (String)lines.get(i);
      if (!line.startsWith("#")) {
        Iterable parts = this.PROPERTY_SPLITTER.split(line);
        if (Iterables.size(parts) == 2) {
          PropertyItemVO item = new PropertyItemVO((String)Iterables.getFirst(parts, null), (String)Iterables.getLast(parts));
          if ((previousLine != null) && (previousLine.startsWith("#"))) {
            item.setComment(StringUtils.trimLeadingCharacter(previousLine, '#').trim());
          }
          items.add(item);
        }
      }

      previousLine = line;
    }
    return items;
  }

  public void propertyZipUpload(FileUploadEvent event)
  {
    String fileName = event.getFile().getFileName();
    LOGGER.info("Deal uploaded file: {}", fileName);
    ZipInputStream zipInputStream = null;
    try {
      zipInputStream = new ZipInputStream(event.getFile().getInputstream());
      ZipEntry nextEntry = null;
      while ((nextEntry = zipInputStream.getNextEntry()) != null) {
        String entryName = nextEntry.getName();
        savePropertyGroup(entryName, Files.getNameWithoutExtension(entryName), zipInputStream);
      }
    } catch (IOException e) {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Upload File error.", fileName));
      LOGGER.error("Upload File Exception.", e);
    } finally {
      if (zipInputStream != null)
        try {
          zipInputStream.close();
        }
        catch (IOException e)
        {
        }
    }
  }

  @Deprecated
  public void propertyZipUploadOld(FileUploadEvent event)
  {
    String fileName = event.getFile().getFileName();
    LOGGER.info("Deal uploaded file: {}", fileName);
    ZipInputStream zipInputStream = null;
    try {
      zipInputStream = new ZipInputStream(event.getFile().getInputstream());
      ZipEntry nextEntry = null;
      while ((nextEntry = zipInputStream.getNextEntry()) != null) {
        String entryName = nextEntry.getName();
        savePropertyGroupOld(entryName, Files.getNameWithoutExtension(entryName), zipInputStream);
      }
    } catch (IOException e) {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Upload File error.", fileName));
      LOGGER.error("Upload File Exception.", e);
    } finally {
      if (zipInputStream != null)
        try {
          zipInputStream.close();
        }
        catch (IOException e)
        {
        }
    }
  }

  @Deprecated
  private void savePropertyGroupOld(String fileName, String group, InputStream inputstream) throws IOException {
    Reader reader = new InputStreamReader(inputstream, Charsets.UTF_8);
    Properties properties = new Properties();
    properties.load(reader);
    if (!properties.isEmpty()) {
      String authedNode = ZKPaths.makePath(this.nodeAuth.getAuthedNode(), this.versionMB.getSelectedVersion());
      String groupPath = ZKPaths.makePath(authedNode, group);
      boolean created = this.nodeService.createProperty(groupPath, null);
      if (created) {
        Map<String, String> map = Maps.fromProperties(properties);
        for (Map.Entry<String, String> entry : map.entrySet()) {
          this.nodeService.createProperty(ZKPaths.makePath(groupPath, (String)entry.getKey()), (String)entry.getValue());
        }
        refreshGroup();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Succesful", fileName + " is uploaded."));
      } else {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Create group with file error.", fileName));
      }
    }
    else {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "File is empty.", fileName));
    }
  }

  public void notified(String key, String value)
  {
    refreshGroup();
  }

  public void refreshGroup() {
    String rootNode = this.nodeAuth.getAuthedNode();
    String version = this.versionMB.getSelectedVersion();
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Initialize menu for authed node: {} in version {}", rootNode, version);
    }

    if ((!Strings.isNullOrEmpty(rootNode)) && (!Strings.isNullOrEmpty(version)))
      this.propertyGroups = this.nodeService.listChildren(ZKPaths.makePath(rootNode, version));
    else {
      this.propertyGroups = null;
    }

    this.selectedGroup = null;

    this.nodeData.refreshNodeProperties(null);
  }
}