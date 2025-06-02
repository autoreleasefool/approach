import BowlersRepositoryInterface
import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import RecentlyUsedServiceInterface
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary

@Reducer
public struct Bowlers: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		@Shared(.bowlersFetchRequest) public var bowlersFetchRequest
		public var bowlers: IdentifiedArrayOf<Bowler.List> = []
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case didStartTask
			case didTapBowler(Bowler.ID)
		}

		@CasePathable
		public enum Internal {
			case didLoadBowlers(Result<[Bowler.List], Error>)
			case restartObservation(Bowler.List.FetchRequest)
		}

		@CasePathable
		public enum Delegate {
			case didSelectBowler(Bowler.List)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	enum CancelID: Sendable { case observation }

	@Dependency(BowlersRepository.self) var bowlers
	@Dependency(RecentlyUsedService.self) var recentlyUsed

	public var body: some ReducerOf<Self> {
		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didStartTask:
					return .run { [request = state.$bowlersFetchRequest] send in
						for await query in request.publisher.values {
							await send(.internal(.restartObservation(query)))
						}
					}

				case let .didTapBowler(id):
					guard let bowler = state.bowlers[id: id] else {
						// TODO: Handle error when bowler is not found
						return .none
					}

					return .merge(
						recentlyUsed.didRecentlyUse(.bowlers, id: id, in: self),
						.send(.delegate(.didSelectBowler(bowler)))
					)
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .didLoadBowlers(.success(bowlers)):
					state.bowlers = .init(uniqueElements: bowlers)
					return .none

				case .didLoadBowlers(.failure):
					// TODO: Handle error loading bowlers
					return .none

				case let .restartObservation(query):
					return .run { send in
						for try await bowlers in bowlers.list(ordered: query) {
							await send(.internal(.didLoadBowlers(.success(bowlers))))
						}
					} catch: { error, send in
						await send(.internal(.didLoadBowlers(.failure(error))))
					}
					.cancellable(id: CancelID.observation, cancelInFlight: true)
				}

			case .delegate:
				return .none
			}
		}
	}
}

@ViewAction(for: Bowlers.self)
public struct BowlersView: View {
	public let store: StoreOf<Bowlers>

	init(store: StoreOf<Bowlers>) {
		self.store = store
	}

	public var body: some View {
		Section(Strings.Overview.List.bowlers) {
			ForEach(store.bowlers) { bowler in
				Button { send(.didTapBowler(bowler.id)) } label: {
					LabeledContent(bowler.name, value: format(average: bowler.average))
				}
				.buttonStyle(.navigation)
			}

			if store.bowlers.isEmpty {
				// TODO: Handle empty state
				Text(Strings.Overview.List.bowlers)
			}
		}
		.task { await send(.didStartTask).finish() }
	}
}
