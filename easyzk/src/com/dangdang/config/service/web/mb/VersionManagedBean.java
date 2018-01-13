package com.dangdang.config.service.web.mb;

import com.dangdang.config.service.INodeService;
import com.dangdang.config.service.entity.PropertyItem;
import com.dangdang.config.service.observer.IObserver;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.apache.curator.utils.ZKPaths;
import org.primefaces.component.inputtext.InputText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedBean(name="versionMB")
@SessionScoped
public class VersionManagedBean
  implements IObserver, Serializable
{
  private static final long serialVersionUID = 1L;

  @ManagedProperty("#{nodeService}")
  private INodeService nodeService;

  @ManagedProperty("#{nodeAuthMB}")
  private NodeAuthManagedBean nodeAuth;
  private static final Logger LOGGER = LoggerFactory.getLogger(VersionManagedBean.class);
  private String selectedVersion;
  private List<String> versions;
  private InputText versionToBeCreatedInput;
  private InputText versionToCloneInput;

  public void setNodeService(INodeService nodeService)
  {
    this.nodeService = nodeService;
  }

  public void setNodeAuth(NodeAuthManagedBean nodeAuth)
  {
    this.nodeAuth = nodeAuth;
  }

  @PostConstruct
  private void init() {
    this.nodeAuth.register(this);
    refresh();
  }

  public void createVersion()
  {
    String versionToBeCreated = (String)this.versionToBeCreatedInput.getValue();
    if (!Strings.isNullOrEmpty(versionToBeCreated))
    {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Create version [{}].", versionToBeCreated);
      }

      boolean suc = this.nodeService.createProperty(ZKPaths.makePath(this.nodeAuth.getAuthedNode(), versionToBeCreated), null);
      if (suc) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Version created.", versionToBeCreated));

        refresh();
        this.selectedVersion = versionToBeCreated;
        this.versionToBeCreatedInput.setValue("");
      } else {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Version creation failed.", versionToBeCreated));
      }
    }
  }

  public void cloneVersion()
  {
    String versionToClone = (String)this.versionToCloneInput.getValue();
    if ((!Strings.isNullOrEmpty(versionToClone)) && (!Strings.isNullOrEmpty(this.selectedVersion)))
    {
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Clone version [{}] from version [{}].", versionToClone, this.selectedVersion);
      }
      cloneTree(ZKPaths.makePath(this.nodeAuth.getAuthedNode(), this.selectedVersion), ZKPaths.makePath(this.nodeAuth.getAuthedNode(), versionToClone));
      cloneTree(ZKPaths.makePath(this.nodeAuth.getAuthedNode(), this.selectedVersion + "$"), ZKPaths.makePath(this.nodeAuth.getAuthedNode(), versionToClone + "$"));

      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Version cloned.", versionToClone));
      refresh();
      this.selectedVersion = versionToClone;
      this.versionToCloneInput.setValue("");
    }
  }

  private void cloneTree(String sourceVersionPath, String destinationVersionPath) {
    List<String> sourceGroups = this.nodeService.listChildren(sourceVersionPath);
    if (sourceGroups != null)
      for (String sourceGroup : sourceGroups) {
        String sourceGroupFullPath = ZKPaths.makePath(sourceVersionPath, sourceGroup);
        String destinationGroupFullPath = ZKPaths.makePath(destinationVersionPath, sourceGroup);

        this.nodeService.createProperty(destinationGroupFullPath, null);
        List<PropertyItem> sourceProperties = this.nodeService.findProperties(sourceGroupFullPath);
        if (sourceProperties != null)
          for (PropertyItem sourceProperty : sourceProperties)
            this.nodeService.createProperty(ZKPaths.makePath(destinationGroupFullPath, sourceProperty.getName()), sourceProperty.getValue());
      }
  }

  public void notified(String rootNode, String value)
  {
    refresh();
  }

  private void refresh() {
    String rootNode = this.nodeAuth.getAuthedNode();
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Initialize versions for authed node: {}", rootNode);
    }

    if (!Strings.isNullOrEmpty(rootNode)) {
      List children = this.nodeService.listChildren(rootNode);
      if (children != null) {
        children = Lists.newArrayList(Iterables.filter(children, new Predicate()
        {
          public boolean apply(Object input)
          {
            return (input != null && !input.toString().endsWith("$"));
          }
        }));
      }
      this.versions = children;
    } else {
      this.versions = null;
    }

    this.selectedVersion = null;
  }

  public final String getSelectedVersion() {
    return this.selectedVersion;
  }

  public final void setSelectedVersion(String selectedVersion) {
    this.selectedVersion = selectedVersion;
  }

  public final List<String> getVersions() {
    return this.versions;
  }

  public final void setVersions(List<String> versions) {
    this.versions = versions;
  }

  public InputText getVersionToBeCreatedInput() {
    return this.versionToBeCreatedInput;
  }

  public void setVersionToBeCreatedInput(InputText versionToBeCreatedInput) {
    this.versionToBeCreatedInput = versionToBeCreatedInput;
  }

  public InputText getVersionToCloneInput() {
    return this.versionToCloneInput;
  }

  public void setVersionToCloneInput(InputText versionToCloneInput) {
    this.versionToCloneInput = versionToCloneInput;
  }
}