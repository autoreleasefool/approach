import AnalyticsServiceInterface
import BowlersRepositoryInterface
import ComposableArchitecture
import ExtensionsPackageLibrary
import FeatureActionLibrary
import ModelsLibrary

@Reducer
public struct Onboarding: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public var step: Step = .empty
		public var isAddingBowler = false

		public var isShowingSheet = false
		public var bowlerName = ""

		public init() {}
	}

	public enum Action: FeatureAction, BindableAction, ViewAction {
		@CasePathable
		public enum View {
			case onAppear
			case didFirstAppear
			case didTapGetStarted
			case didTapAddBowler
		}
		@CasePathable
		public enum Delegate {
			case didFinishOnboarding
		}
		@CasePathable
		public enum Internal {
			case nextStep
			case didCreateBowler(Result<Never, Error>)
		}

		case view(View)
		case `internal`(Internal)
		case delegate(Delegate)
		case binding(BindingAction<State>)
	}

	public enum Step: Equatable, CaseIterable {
		case empty
		case header
		case headerMessage
		case headerMessageCrafted
		case headerMessageCraftedGetStarted

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

	public init() {}

	@Dependency(BowlersRepository.self) var bowlers
	@Dependency(\.continuousClock) var clock
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		BindingReducer()

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didFirstAppear:
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
							try await bowlers.create(bowler)
							await send(.delegate(.didFinishOnboarding))
						} catch {
							await send(.internal(.didCreateBowler(.failure(error))))
						}
					}
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

			case .delegate, .binding:
				return .none
			}
		}

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}

		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			case let .internal(.didCreateBowler(.failure(error))):
				return error
			default:
				return nil
			}
		}
	}
}
