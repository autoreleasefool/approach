import ComposableArchitecture
import FeatureActionLibrary
import StatisticsWidgetsLibrary
import SwiftUI

public struct StatisticsWidgetLayoutBuilder: Reducer {
	public struct State: Equatable {
		@PresentationState public var editor: StatisticsWidgetEditor.State?
		public init() {}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapAddNew
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {
			case editor(PresentationAction<StatisticsWidgetEditor.Action>)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public init() {}

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapAddNew:
					state.editor = .init(existingConfiguration: nil)
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .editor(.presented(.delegate(delegateAction))):
					switch delegateAction {
					case let .didCreateConfiguration(configuration):
						return .none
					}

				case .editor(.dismiss), .editor(.presented(.internal)), .editor(.presented(.view)):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$editor, action: /Action.internal..Action.InternalAction.editor) {
			StatisticsWidgetEditor()
		}
	}
}
