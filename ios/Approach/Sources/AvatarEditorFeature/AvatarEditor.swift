import ComposableArchitecture
import FeatureActionLibrary
import SharedModelsLibrary
import SwiftUI

public struct AvatarEditor: ReducerProtocol {
	public struct State: Equatable {
		public var name: String
		public var avatar: Avatar

		public init(name: String, avatar: Avatar) {
			self.name = name
			self.avatar = avatar
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTap
		}
		public enum DelegateAction: Equatable {}
		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public init() {}

	public var body: some ReducerProtocol<State, Action> {
		Reduce<State, Action> { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTap:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .never:
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}
