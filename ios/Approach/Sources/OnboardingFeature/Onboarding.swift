import BowlersRepositoryInterface
import ComposableArchitecture
import ExtensionsLibrary
import FeatureActionLibrary
import ModelsLibrary

public struct Onboarding: Reducer {
	public struct State: Equatable {
		public var step: Step = .empty
		public var isShowingSheet = false
		public var bowlerName = ""
		public var isAddingBowler = false

		public init() {}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didAppear
			case didTapGetStarted
			case didTapAddBowler
			case didChangeBowlerName(String)
			case setSheet(isShowing: Bool)
		}
		public enum DelegateAction: Equatable {
			case didFinishOnboarding
		}
		public enum InternalAction: Equatable {
			case nextStep
			case didCreateBowler(TaskResult<Never>)
		}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public enum Step: Equatable, CaseIterable {
		case empty
		case header
		case headerMessage
		case headerMessageCrafted
		case headerMessageCraftedGetStarted
	}

	public init() {}

	@Dependency(\.bowlers) var bowlers
	@Dependency(\.continuousClock) var clock
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didAppear:
					return .run { send in
						try await clock.sleep(for: .milliseconds(300))
						for _ in Step.allCases.dropFirst() {
							await send(.internal(.nextStep))
							try await clock.sleep(for: .milliseconds(800))
						}
					}
					.animation(.easeIn)

				case .didTapGetStarted:
					state.isShowingSheet = true
					return .none

				case .didTapAddBowler:
					guard !state.bowlerName.trimmingCharacters(in: .whitespaces).isEmpty, !state.isAddingBowler else { return .none }
					state.isAddingBowler = true
					var bowler = Bowler.Create.defaultBowler(withId: uuid())
					bowler.name = state.bowlerName.trimmingCharacters(in: .whitespaces)
					return .run { [bowler = bowler] send in
						do {
							try await self.bowlers.create(bowler)
							await send(.delegate(.didFinishOnboarding))
						} catch {
							await send(.internal(.didCreateBowler(.failure(error))))
						}
					}

				case let .didChangeBowlerName(text):
					state.bowlerName = text
					return .none

				case let .setSheet(isShowing):
					state.isShowingSheet = isShowing
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case .nextStep:
					state.step.toNext()
					return .none

				case .didCreateBowler(.failure):
					state.isAddingBowler = false
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

extension Onboarding.Step {
	var isShowingHeader: Bool {
		switch self {
		case .empty:
			return false
		case .header, .headerMessage, .headerMessageCrafted, .headerMessageCraftedGetStarted:
			return true
		}
	}
	var isShowingMessage: Bool {
		switch self {
		case .empty, .header:
			return false
		case .headerMessage, .headerMessageCrafted, .headerMessageCraftedGetStarted:
			return true
		}
	}
	var isShowingGetStarted: Bool {
		switch self {
		case .empty, .header, .headerMessage, .headerMessageCrafted:
			return false
		case .headerMessageCraftedGetStarted:
			return true
		}
	}
	var isShowingCrafted: Bool {
		switch self {
		case .empty, .header, .headerMessage:
			return false
		case .headerMessageCrafted, .headerMessageCraftedGetStarted:
			return true
		}
	}
}
