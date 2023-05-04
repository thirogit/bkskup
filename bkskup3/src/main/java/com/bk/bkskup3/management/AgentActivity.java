package com.bk.bkskup3.management;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.bk.bkskup3.BkActivity;
import com.bk.bkskup3.R;
import com.bk.bkskup3.dao.BkStore;
import com.bk.bkskup3.dao.SettingsStore;
import com.bk.bkskup3.feedback.ErrorToast;
import com.bk.bkskup3.model.Agent;
import com.bk.bkskup3.model.AgentObj;
import com.bk.bkskup3.settings.AgentAsSettings;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: SG0891787
 * Date: 11/9/2014
 * Time: 8:52 PM
 */
public class AgentActivity extends BkActivity {

    private EditText mAgentCodeEditBox;
    private EditText mAgentNameEditBox;
    private EditText mPlateNoEditBox;

    @Inject
    SettingsStore mStore;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agent);

        mAgentCodeEditBox = (EditText) findViewById(R.id.agentCodeEditBox);
        mAgentNameEditBox = (EditText) findViewById(R.id.agentNameEditBox);
        mPlateNoEditBox = (EditText) findViewById(R.id.plateNoEditBox);

        setInputs(mStore.getAgent());
    }


    protected void setInputs(Agent agent)
    {
        mAgentCodeEditBox.setText(agent.getCode());
        mAgentNameEditBox.setText(agent.getName());
        mPlateNoEditBox.setText(agent.getPlateNo());
    }

    protected AgentObj createAgent()
    {
        AgentObj agent = new AgentObj();

        agent.setAgentCode(mAgentCodeEditBox.getText().toString());
        agent.setAgentName(mAgentNameEditBox.getText().toString());
        agent.setPlateNo(mPlateNoEditBox.getText().toString());

        return agent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menuSave:
                if (validate())
                {
                    saveAgent(createAgent());
                    setResult(RESULT_OK);
                    finish();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveAgent(AgentObj agent) {
        mStore.saveAgent(agent);

        AgentAsSettings agentAsSettings = new AgentAsSettings();
        agentAsSettings.copyFrom(agent);

        Intent intent = new Intent(AgentAsSettings.ACTION_AGENT_CHANGED);
        intent.putExtra(AgentAsSettings.EXTRA_LATEST_AGENT,agentAsSettings);

        sendBroadcast(intent);
    }

    private boolean validate() {

        ErrorToast toast = new ErrorToast(this);
        if(mAgentCodeEditBox.getText().length() == 0)
        {
            toast.show(R.string.errEnterAgentCode);
            return false;
        }
        if(mPlateNoEditBox.getText().length() == 0)
        {
            toast.show(R.string.errEnterAgentPlateNo);
            return false;
        }

        return true;
    }


}
