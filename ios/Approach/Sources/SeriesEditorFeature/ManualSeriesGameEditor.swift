import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import StringsLibrary
import SwiftUI

@Reducer
public struct ManualSeriesGameEditor: Reducer, Sendable {
	@ObservableState
	public struct State: Identifiable, Equatable {
		public let id: Game.ID
		public let index: Int
		public var score: Int = 0

		public init(id: Game.ID, index: Int) {
			self.id = id
			self.index = index
		}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable
		public enum View { case doNothing }
		@CasePathable
		public enum Delegate { case doNothing }
		@CasePathable
		public enum Internal { case doNothing }

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	public var body: some ReducerOf<Self> {
		BindingReducer()

		Reduce<State, Action> { state, action in
			switch action {
			case .view(.doNothing):
				return .none

			case .internal(.doNothing):
				return .none

			case .binding(\.score):
				state.score = min(max(state.score, 0), Game.MAXIMUM_SCORE)
				return .none

			case .delegate, .binding:
				return .none
			}
		}
	}
}

@ViewAction(for: ManualSeriesGameEditor.self)
public struct ManualSeriesGameEditorView: View {
	@Bindable public var store: StoreOf<ManualSeriesGameEditor>

	public var body: some View {
		TextField(
			Strings.Series.Editor.Fields.Manual.scoreForGameOrdinal(store.index + 1),
			value: $store.score,
			formatter: NumberFormatter()
		)
		.keyboardType(.numberPad)
	}
}
