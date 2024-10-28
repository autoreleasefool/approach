//
//  BadgesObserver.swift
//  Approach
//
//  Created by Joseph Roque on 2024-10-05.
//

import BadgesServiceInterface
import ComposableArchitecture
import FeatureActionLibrary
import FeatureFlagsLibrary
import SwiftUI
import ToastLibrary

@Reducer
public struct BadgesObserver: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var badgesQueue: [ToastState<BadgeAction>] = []

		@Presents public var badgeToast: ToastState<BadgeAction>?

		public init() {}
	}

	public enum Action: FeatureAction {
		@CasePathable
		public enum View {
			case didStartTask
		}
		@CasePathable
		public enum Delegate { case doNothing }
		@CasePathable
		public enum Internal {
			case showNextBadge
			case didEarnBadge(ToastState<BadgeAction>)
			case badgeToast(PresentationAction<BadgeAction>)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	public enum BadgeAction: Equatable, ToastableAction {
		case didDismiss
		case didFinishDismissing
	}

	public init() {}

	@Dependency(\.continuousClock) var clock
	@Dependency(\.uuid) var uuid
	@Dependency(BadgesService.self) var badges
	@Dependency(\.featureFlags) var featureFlags

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didStartTask:
					guard featureFlags.isFlagEnabled(.badges) else { return .none }
					return .run { send in
						for await earned in badges.observeNewBadges() {
							let title = type(of: earned).title
							let toast: ToastState<BadgeAction> = ToastState(content: .badge(.init(title: title)))
							await send(.internal(.didEarnBadge(toast)))
						}
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case .showNextBadge:
					return showNextBadge(state: &state)

				case let .didEarnBadge(badge):
					state.badgesQueue.insert(badge, at: 0)
					return showNextBadge(state: &state)

				case let .badgeToast(.presented(badgeAction)):
					switch badgeAction {
					case .didDismiss:
						state.badgeToast = nil
						return .none

					case .didFinishDismissing:
						state.badgeToast = nil
						return .run { send in
							try await clock.sleep(for: .seconds(0.5))
							await send(.internal(.showNextBadge))
						}
					}

				case .badgeToast(.dismiss):
					state.badgeToast = nil
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}

	private func showNextBadge(state: inout State) -> Effect<Action> {
		guard let next = state.badgesQueue.popLast() else {
			return .none
		}

		state.badgeToast = next
		return .none
	}
}

public struct BadgesObserverViewModifier: ViewModifier {
	@SwiftUI.State var store: StoreOf<BadgesObserver>

	public func body(content: Content) -> some View {
		content
			.task { await store.send(.view(.didStartTask)).finish() }
			.toast($store.scope(state: \.badgeToast, action: \.internal.badgeToast))
	}
}

extension View {
	public func observeBadges(store: StoreOf<BadgesObserver>) -> some View {
		self.modifier(BadgesObserverViewModifier(store: store))
	}
}
