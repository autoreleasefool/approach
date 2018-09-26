package ca.josephroque.bowlingcompanion.teams.list

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import ca.josephroque.bowlingcompanion.App
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.common.Android
import ca.josephroque.bowlingcompanion.common.adapters.BaseRecyclerViewAdapter
import ca.josephroque.bowlingcompanion.common.adapters.NameAverageRecyclerViewAdapter
import ca.josephroque.bowlingcompanion.common.fragments.BaseDialogFragment
import ca.josephroque.bowlingcompanion.teams.Team
import ca.josephroque.bowlingcompanion.teams.teammember.TeamMember
import ca.josephroque.bowlingcompanion.utils.Analytics
import ca.josephroque.bowlingcompanion.utils.Color
import ca.josephroque.bowlingcompanion.utils.safeLet
import kotlinx.android.synthetic.main.dialog_team.btn_delete as deleteButton
import kotlinx.android.synthetic.main.dialog_team.input_name as nameInput
import kotlinx.android.synthetic.main.dialog_team.list_bowlers as bowlersList
import kotlinx.android.synthetic.main.dialog_team.toolbar_team as teamToolbar
import kotlinx.android.synthetic.main.dialog_team.tv_error_no_bowlers as noBowlersError
import kotlinx.android.synthetic.main.dialog_team.view.*
import kotlinx.coroutines.experimental.launch

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Dialog to create a new team.
 */
class TeamDialog : BaseDialogFragment(),
        BaseRecyclerViewAdapter.AdapterDelegate<Bowler> {

    companion object {
        @Suppress("unused")
        private const val TAG = "TeamDialog"

        private const val ARG_TEAM = "${TAG}_TEAM"

        fun newInstance(team: Team?): TeamDialog {
            val dialog = TeamDialog()
            dialog.arguments = Bundle().apply { team?.let { putParcelable(ARG_TEAM, team) } }
            return dialog
        }
    }

    private var team: Team? = null
    private var delegate: TeamDialogDelegate? = null
    private lateinit var bowlerAdapter: NameAverageRecyclerViewAdapter<Bowler>

    private val selectedBowlers: List<TeamMember>
        get() {
            val selected = bowlerAdapter.selectedItems
            val list: MutableList<TeamMember> = ArrayList()
            selected.forEach {
                list.add(TeamMember(
                        teamId = team?.id ?: -1,
                        bowlerName = it.name,
                        bowlerId = it.id
                ))
            }
            return list
        }

    private val onClickListener = View.OnClickListener {
        safeLet(context, team) { context, team ->
            AlertDialog.Builder(context)
                    .setTitle(String.format(context.resources.getString(R.string.query_delete_item), team.name))
                    .setMessage(R.string.dialog_delete_item_message)
                    .setPositiveButton(R.string.delete) { _, _ ->
                        delegate?.onDeleteTeam(team)
                        dismiss()
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
        }
    }

    // MARK: Lifecycle functions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        team = arguments?.getParcelable(ARG_TEAM)

        val rootView = inflater.inflate(R.layout.dialog_team, container, false)
        team?.let { resetInputs(it, rootView) }
        setupToolbar(rootView)
        setupBowlers(rootView)
        setupInput(rootView)
        return rootView
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val parent = parentFragment as? TeamDialogDelegate
                ?: throw RuntimeException("${parentFragment!!} must implement TeamDialogDelegate")
        delegate = parent
    }

    override fun onDetach() {
        super.onDetach()
        delegate = null
    }

    override fun onStart() {
        super.onStart()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        activity?.let {
            nameInput.clearFocus()
            App.hideSoftKeyBoard(it)
        }

        team?.let { deleteButton.visibility = View.VISIBLE }
        nameInput.text?.let { nameInput.setSelection(it.length) }
        refreshBowlerList()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun dismiss() {
        activity?.let {
            App.hideSoftKeyBoard(it)
            it.supportFragmentManager?.popBackStack()
        }

        super.dismiss()
    }

    // MARK: Private functions

    private fun setupToolbar(rootView: View) {
        if (team == null) {
            rootView.toolbar_team.setTitle(R.string.new_team)
        } else {
            rootView.toolbar_team.setTitle(R.string.edit_team)
        }

        rootView.toolbar_team.apply {
            inflateMenu(R.menu.dialog_team)
            setNavigationIcon(R.drawable.ic_dismiss)
            setNavigationOnClickListener {
                dismiss()
            }
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_save -> {
                        saveTeam()
                        true
                    }
                    else -> super.onOptionsItemSelected(it)
                }
            }
        }
    }

    private fun setupBowlers(rootView: View) {
        val context = context ?: return

        bowlerAdapter = NameAverageRecyclerViewAdapter(emptyList(), this)
        bowlerAdapter.multiSelect = true

        rootView.list_bowlers.layoutManager = LinearLayoutManager(context)
        rootView.list_bowlers.adapter = bowlerAdapter
        BaseRecyclerViewAdapter.applyDefaultDivider(rootView.list_bowlers, context)
    }

    private fun setupInput(rootView: View) {
        rootView.btn_delete.setOnClickListener(onClickListener)
        rootView.input_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSaveButton()
            }
        })
    }

    private fun canSave(): Boolean {
        val name = nameInput.text.toString()
        val members = selectedBowlers

        return name.isNotEmpty() && members.isNotEmpty()
    }

    private fun updateSaveButton() {
        val saveButton = teamToolbar.menu.findItem(R.id.action_save)
        if (canSave()) {
            saveButton?.isEnabled = true
            saveButton?.icon?.alpha = Color.ALPHA_ENABLED
        } else {
            saveButton?.isEnabled = false
            saveButton?.icon?.alpha = Color.ALPHA_DISABLED
        }
    }

    private fun saveTeam() {
        launch(Android) {
            this@TeamDialog.context?.let { context ->
                val name = nameInput.text.toString()

                if (canSave()) {
                    val oldTeam = team
                    val (newTeam, error) = if (oldTeam != null) {
                        Team.save(context, oldTeam.id, name, selectedBowlers).await()
                    } else {
                        Team.save(context, -1, name, selectedBowlers).await()
                    }

                    if (error != null) {
                        error.show(context)
                        oldTeam?.let { resetInputs(it) }
                        refreshBowlerList()
                    } else if (newTeam != null) {
                        dismiss()
                        delegate?.onFinishTeam(newTeam)

                        if (oldTeam == null) {
                            Analytics.trackCreateTeam(selectedBowlers.size)
                        } else {
                            Analytics.trackEditTeam()
                        }
                    }
                }
            }
        }
    }

    private fun refreshBowlerList() {
        val context = context ?: return
        launch(Android) {
            val bowlers = Bowler.fetchAll(context).await()
            if (bowlers.isEmpty()) {
                bowlersList.visibility = View.GONE
                noBowlersError.visibility = View.VISIBLE
            } else {
                bowlersList.visibility = View.VISIBLE
                noBowlersError.visibility = View.GONE
            }

            val ids: MutableSet<Long> = HashSet()
            team?.members?.forEach {
                ids.add(it.bowlerId)
            }
            bowlerAdapter.items = bowlers
            bowlerAdapter.setSelectedElementsWithIds(ids)
            updateSaveButton()
        }
    }

    private fun resetInputs(team: Team, rootView: View? = null) {
        val nameInput = rootView?.input_name ?: this.nameInput
        nameInput.setText(team.name)
    }

    // MARK: AdapterDelegate

    override fun onItemClick(item: Bowler) {
        updateSaveButton()
    }

    override fun onItemDelete(item: Bowler) {}
    override fun onItemLongClick(item: Bowler) {}
    override fun onItemSwipe(item: Bowler) {}

    // MARK: TeamDialogDelegate

    interface TeamDialogDelegate {
        fun onFinishTeam(team: Team)
        fun onDeleteTeam(team: Team)
    }
}
