package com.bk.bkskup3.tasks;

import com.bk.bkskup3.settings.InputDefaultsSettings;
import com.bk.bkskup3.settings.RepoSettings;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 12/31/2014
 * Time: 11:16 AM
 */
public class DependenciesForHents {

    private InputDefaultsSettings inputDefaults;
    private RepoSettings repoSettings;

    public InputDefaultsSettings getInputDefaults() {
        return inputDefaults;
    }

    public void setInputDefaults(InputDefaultsSettings inputDefaults) {
        this.inputDefaults = inputDefaults;
    }

    public RepoSettings getRepoSettings() {
        return repoSettings;
    }

    public void setRepoSettings(RepoSettings repoSettings) {
        this.repoSettings = repoSettings;
    }
}
