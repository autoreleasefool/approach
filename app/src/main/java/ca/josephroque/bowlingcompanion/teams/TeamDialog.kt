package ca.josephroque.bowlingcompanion.teams

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
import ca.josephroque.bowlingcompanion.utils.Color
import ca.josephroque.bowlingcompanion.utils.safeLet
import kotlinx.android.synthetic.main.dialog_team.*
import kotlinx.android.synthetic.main.dialog_team.view.*
import kotlinx.coroutines.experimental.launch


/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Dialog to create a new team.
 */
class TeamDialog : DialogFragment(),
        View.OnClickListener,
        BaseRecyclerViewAdapter.OnAdapterInteractionListener<Bowler> {

    companion object {
        /** Logging identifier. */
        private const val TAG = "TeamDialog"

        /** Identifier for the [Team] to be edited. */
        private const val ARG_TEAM = "${TAG}_TEAM"

        fun newInstance(team: Team?): TeamDialog {
            val dialog = TeamDialog()
            val args = Bundle()
            team?.let { args.putParcelable(ARG_TEAM, team) }
            dialog.arguments = args
            return dialog
        }
    }

    /** Team to be edited, or null if a new team is to be created. */
    private var team: Team? = null

    /** Interaction handler. */
    private var listener: OnTeamDialogInteractionListener? = null

    /** Adapter to manage rendering the list of bowlers. */
    private var bowlerAdapter: NameAverageRecyclerViewAdapter<Bowler>? = null

    /** Bowlers to display. */
    private var bowlers: MutableList<Bowler> = ArrayList()

    /** Current list of selected bowlers. */
    private val selectedBowlers: MutableList<Pair<String, Long>>?
        get() {
            val selected = bowlerAdapter?.selectedItems ?: return null
            val list: MutableList<Pair<String, Long>> = ArrayList()
            selected.forEach({
                list.add(Pair(it.name, it.id))
            })
            return list
        }

    /** @Override */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        team = arguments?.getParcelable(ARG_TEAM) ?: savedInstanceState?.getParcelable(ARG_TEAM)

        val rootView = inflater.inflate(R.layout.dialog_team, container, false)
        val context = context ?: return rootView

        bowlerAdapter = NameAverageRecyclerViewAdapter(emptyList(), this)
        bowlerAdapter?.multiSelect = true

        if (team == null) {
            rootView.toolbar_team.setTitle(R.string.new_team)
        } else {
            rootView.toolbar_team.setTitle(R.string.edit_team)
        }


        rootView.list_bowlers.layoutManager = LinearLayoutManager(context)
        rootView.list_bowlers.adapter = bowlerAdapter
        BaseRecyclerViewAdapter.applyDefaultDivider(rootView.list_bowlers, context)

        rootView.btn_delete.setOnClickListener(this)
        rootView.toolbar_team.apply {
            inflateMenu(R.menu.menu_dialog_team)
            setNavigationIcon(R.drawable.ic_close_white_24dp)
            setNavigationOnClickListener {
                dismiss()
            }
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_save -> {
                        saveTeam()
                        true
                    }
                    else -> false
                }
            }
        }
        rootView.input_name.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSaveButton()
            }
        })

        return rootView
    }

    /** @Override */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        context as? OnTeamDialogInteractionListener ?: throw RuntimeException(context!!.toString() + " must implement OnTeamDialogInteractionListener")
        listener = context
    }

    /** @Override */
    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /** @Override */
    override fun onResume() {
        super.onResume()

        activity?.let {
            input_name.clearFocus()
            App.hideSoftKeyBoard(it)
        }


        team?.let {
            btn_delete.visibility = View.VISIBLE
            input_name.setText(it.name)
        }

        input_name.setSelection(input_name.text.length)
        refreshBowlerList()
    }

    /** @Override */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(ARG_TEAM, team)
    }

    /** @Override */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    /** @Override */
    override fun onClick(v: View?) {
        safeLet(context, team) { context, team ->
            AlertDialog.Builder(context)
                    .setTitle(String.format(context.resources.getString(R.string.query_delete_item), team.name))
                    .setMessage(R.string.dialog_delete_item_message)
                    .setPositiveButton(R.string.delete, { _, _ ->
                        listener?.onDeleteTeam(team)
                        dismiss()
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show()
        }
    }

    /**
     * Clean up dialog before calling super.
     */
    override fun dismiss() {
        activity?.let {
            App.hideSoftKeyBoard(it)
            it.supportFragmentManager?.popBackStack()
        }

        super.dismiss()
    }

    /**
     *
     */
    private fun canSave(): Boolean {
        val name = input_name.text.toString()
        val members = selectedBowlers

        return name.isNotEmpty() && (members?.size ?: 0) > 0
    }

    /**
     * Update save button state based on text entered.
     */
    private fun updateSaveButton() {
        val saveButton = toolbar_team.menu.findItem(R.id.action_save)
        if (canSave()) {
            saveButton?.isEnabled = true
            saveButton?.icon?.alpha = Color.ALPHA_ENABLED
        } else {
            saveButton?.isEnabled = false
            saveButton?.icon?.alpha = Color.ALPHA_DISABLED
        }
    }

    /**
     * Save the current team. Show errors if there are any.
     */
    private fun saveTeam() {
        launch(Android) {
            this@TeamDialog.context?.let { context ->
                val oldName = team?.name ?: ""
                val oldMembers = team?.members ?: ArrayList()

                val name = input_name.text.toString()
                val members = selectedBowlers

                members?.let {
                    if (canSave()) {
                        val newTeam = team ?: Team(-1, name, it)
                        newTeam.name = name
                        newTeam.members = it

                        val error = newTeam.save(context).await()
                        if (error != null) {
                            error.show(context)
                            newTeam.name = oldName
                            newTeam.members = oldMembers
                            input_name.setText(oldName)
                            refreshBowlerList()
                        } else {
                            dismiss()
                            listener?.onFinishTeam(newTeam)
                        }
                    }
                }
            }
        }
    }

    /**
     * Reload the list of bowlers and update list.
     */
    private fun refreshBowlerList() {
        val context = context?: return
        launch(Android) {
            val bowlers = Bowler.fetchAll(context).await()
            this@TeamDialog.bowlers = bowlers
            bowlerAdapter?.setElements(bowlers)

            if (bowlers.isEmpty()) {
                list_bowlers.visibility = View.GONE
                tv_error_no_bowlers.visibility = View.VISIBLE
            } else {
                list_bowlers.visibility = View.VISIBLE
                tv_error_no_bowlers.visibility = View.GONE
            }

            val ids: MutableSet<Long> = HashSet()
            team?.members?.forEach({
                ids.add(it.second)
            })
            bowlerAdapter?.setSelectedElementsWithIds(ids)
            updateSaveButton()
        }
    }

    /** @Override */
    override fun onItemClick(item: Bowler) {
        updateSaveButton()
    }

    /** Not used. */
    override fun onItemDelete(item: Bowler) {}

    /** Not used. */
    override fun onItemLongClick(item: Bowler) {}

    /** Not used. */
    override fun onItemSwipe(item: Bowler) {}

    /**
     * Handles interactions with the dialog.
     */
    interface OnTeamDialogInteractionListener {

        /**
         * Indicates when the user has finished editing the [Team]
         *
         * @param team the finished [Team]
         */
        fun onFinishTeam(team: Team)

        /**
         * Indicates the user wishes to delete the [Team].
         *
         * @param team the deleted [Team]
         */
        fun onDeleteTeam(team: Team)
    }
}
