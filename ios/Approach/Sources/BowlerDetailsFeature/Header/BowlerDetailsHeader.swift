import BowlerEditorFeature
import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import SwiftUI

@Reducer
public struct BowlerDetailsHeader: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public let bowler: Bowler.Summary

		@Presents public var destination: Destination.State?

		public init(bowler: Bowler.Summary) {
			self.bowler = bowler
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable public enum View {
			case didStartTask
			case didTapOverflowMenuButton
			case didTapEditButton
		}

		@CasePathable public enum Delegate { case doNothing }

		@CasePathable public enum Internal {
			case destination(PresentationAction<Destination.Action>)
		}

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
	}

	@Reducer(state: .equatable)
	public enum Destination {
		case editor(BowlerEditor)
	}

	public init() {}

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didStartTask:
					return .none

				case .didTapEditButton:
					return .none

				case .didTapOverflowMenuButton:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .destination(.presented(.editor(.delegate(.doNothing)))):
					return .none

				case .destination(.dismiss),
						.destination(.presented(.editor(.binding))),
						.destination(.presented(.editor(.view))),
						.destination(.presented(.editor(.internal))):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: \.internal.destination)
	}
}

@ViewAction(for: BowlerDetailsHeader.self)
public struct BowlerDetailsHeaderView: View {
	@Bindable public var store: StoreOf<BowlerDetailsHeader>

	public init(store: StoreOf<BowlerDetailsHeader>) {
		self.store = store
	}

	public var body: some View {
		Text(store.bowler.name)
			.task { await send(.didStartTask).finish() }
			.editor($store.scope(state: \.destination?.editor, action: \.internal.destination.editor))
	}
}

@MainActor extension View {
	fileprivate func editor(_ store: Binding<StoreOf<BowlerEditor>?>) -> some View {
		sheet(item: store) { (store: StoreOf<BowlerEditor>) in
			NavigationStack {
				BowlerEditorView(store: store)
			}
		}
	}
}
