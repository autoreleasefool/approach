//
//  AchievementsObserver.swift
//  Approach
//
//  Created by Joseph Roque on 2024-10-05.
//

import AchievementsRepositoryInterface
import AnalyticsServiceInterface
import ComposableArchitecture
import FeatureActionLibrary
import FeatureFlagsLibrary
import ModelsLibrary
import SwiftUI
import ToastLibrary

@Reducer
public struct AchievementsObserver: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var achievementsQueue: [ToastState<AchievementAction>] = []

		@Presents public var achievementToast: ToastState<AchievementAction>?

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
			case showNextAchievement
			case achievementEarned(Achievement.Summary)
			case didEarnAchievement(ToastState<AchievementAction>)
			case achievementToast(PresentationAction<AchievementAction>)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	public enum AchievementAction: Equatable, ToastableAction {
		case didDismiss
		case didFinishDismissing
	}

	public init() {}

	@Dependency(\.continuousClock) var clock
	@Dependency(\.uuid) var uuid
	@Dependency(AchievementsRepository.self) var achievements
	@Dependency(\.featureFlags) var featureFlags

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didStartTask:
					guard featureFlags.isFlagEnabled(.achievements) else { return .none }

					return .run { send in
						for await earned in achievements.observeNewAchievements() {
							await send(.internal(.achievementEarned(earned)))

							guard earned.achievement.showToastOnEarn else { continue }
							let toast: ToastState<AchievementAction> = ToastState(content: .achievement(.init(title: earned.title)))
							await send(.internal(.didEarnAchievement(toast)))
						}
					}
				}

			case let .internal(internalAction):
				switch internalAction {
				case .showNextAchievement:
					return showNextAchievement(state: &state)

				case .achievementEarned:
					return .none

				case let .didEarnAchievement(achievement):
					state.achievementsQueue.insert(achievement, at: 0)
					return showNextAchievement(state: &state)

				case let .achievementToast(.presented(achievementAction)):
					switch achievementAction {
					case .didDismiss:
						state.achievementToast = nil
						return .none

					case .didFinishDismissing:
						state.achievementToast = nil
						return .run { send in
							try await clock.sleep(for: .seconds(0.5))
							await send(.internal(.showNextAchievement))
						}
					}

				case .achievementToast(.dismiss):
					state.achievementToast = nil
					return .none
				}

			case .delegate:
				return .none
			}
		}

		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case let .internal(.achievementEarned(earned)):
				return Analytics.Achievement.Earned(achievement: earned.title)
			default:
				return nil
			}
		}
	}

	private func showNextAchievement(state: inout State) -> Effect<Action> {
		guard let next = state.achievementsQueue.popLast() else {
			return .none
		}

		state.achievementToast = next
		return .none
	}
}

public struct AchievementsObserverViewModifier: ViewModifier {
	@SwiftUI.State var store: StoreOf<AchievementsObserver>

	public func body(content: Content) -> some View {
		content
			.task { await store.send(.view(.didStartTask)).finish() }
			.toast($store.scope(state: \.achievementToast, action: \.internal.achievementToast))
	}
}

extension View {
	public func observeAchievements(store: StoreOf<AchievementsObserver>) -> some View {
		self.modifier(AchievementsObserverViewModifier(store: store))
	}
}
