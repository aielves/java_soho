package com.dangdang.config.service.web.mb;

import com.dangdang.config.service.IAuthService;
import com.dangdang.config.service.IRootNodeRecorder;
import com.dangdang.config.service.observer.AbstractSubject;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean(name="nodeAuthMB")
@SessionScoped
public class NodeAuthManagedBean extends AbstractSubject
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private String nodeName;
  private String password;
  private String authedNode;

  @ManagedProperty("#{authService}")
  private IAuthService authService;

  @ManagedProperty("#{rootNodeRecorder}")
  private IRootNodeRecorder rootNodeRecorder;

  public void checkAuth()
  {
    boolean login = this.authService.checkAuth(this.nodeName, this.password);
    FacesContext context = FacesContext.getCurrentInstance();
    if (login) {
      context.addMessage(null, new FacesMessage("Login suc."));
      this.authedNode = this.nodeName;
      notify(this.authedNode, null);
      this.rootNodeRecorder.saveNode(this.authedNode);
    } else {
      context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login fail.", "Authentication not passed."));
    }
  }

  public List<String> complete(String prefix) {
    List tips = Lists.newArrayList();
    List<String> nodes = this.rootNodeRecorder.listNode();
    if (nodes != null) {
      for (String node : nodes) {
        if (node.startsWith(prefix)) {
          tips.add(node);
        }
      }
    }
    return tips;
  }

  public void setNodeName(String nodeName) {
    this.nodeName = nodeName;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setAuthService(IAuthService authService) {
    this.authService = authService;
  }

  public final void setRootNodeRecorder(IRootNodeRecorder rootNodeRecorder) {
    this.rootNodeRecorder = rootNodeRecorder;
  }

  public String getNodeName() {
    return this.nodeName;
  }

  public String getPassword() {
    return this.password;
  }

  public String getAuthedNode() {
    return this.authedNode;
  }

  public void setAuthedNode(String authedNode) {
    this.authedNode = authedNode;
  }
}