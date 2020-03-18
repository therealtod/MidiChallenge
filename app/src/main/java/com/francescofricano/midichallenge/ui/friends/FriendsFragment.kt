package com.francescofricano.midichallenge.ui.friends

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.francescofricano.midichallenge.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [FriendsFragment.OnListFragmentInteractionListener] interface.
 */
class FriendsFragment : Fragment() {

    // TODO: Customize parameters
    private var columnCount = 1

    private var listener: OnListFragmentInteractionListener? = null

    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("midichallengeusers")
    private val LOG_TAG = this.javaClass.name
    private val friends = arrayListOf<Friend>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)
        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = FriendsRecyclerViewAdapter(friends, listener)
                collection
                    .document(FirebaseAuth.getInstance().currentUser!!.uid) // This may fail if user not logged
                    .collection("friends")
                    .addSnapshotListener{snapshot, e ->
                        if (e != null) {
                            Log.w(LOG_TAG, "Listen failed.", e)
                            return@addSnapshotListener
                        }

                        if (snapshot != null) {
                            Log.d(LOG_TAG, "Current data: ${snapshot.documents}")
                            // update friends list
                            view.adapter!!.notifyDataSetChanged()
                            for (d in snapshot.documents) {
                                friends.add(Friend(d.id))
                            }

                        } else {
                            Log.d(LOG_TAG, "Current data: null")
                        }

                    }
            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context

        } else {
            throw RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onListFragmentInteraction(item: Friend)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            FriendsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
