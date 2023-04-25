import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import SwiftUI

public struct AvatarEditor: Reducer {
	public struct State: Equatable {
		public var name: String
		public var avatar: Avatar.Summary
		public let initialAvatar: Avatar.Summary

		public init(name: String, avatar: Avatar.Summary) {
			self.name = name
			self.avatar = avatar
			self.initialAvatar = avatar
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didTapCancel
			case didTapDone
		}
		public enum DelegateAction: Equatable {
			case didFinishEditing
		}
		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public init() {}

	public var body: some Reducer<State, Action> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapCancel:
					state.avatar = state.initialAvatar
					return .task { .delegate(.didFinishEditing) }

				case .didTapDone:
					return .task { .delegate(.didFinishEditing) }
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
