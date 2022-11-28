import AlleysDataProviderInterface
import ComposableArchitecture
import SharedModelsLibrary
import ViewsLibrary

public struct AlleyPicker: ReducerProtocol {
	public struct State: Equatable {
		public var alleys: IdentifiedArrayOf<Alley>?
		public var selected: Set<Alley.ID>
		public var limit: Int
		public var error: ListErrorContent?

		public init(selected: Set<Alley.ID>, limit: Int = 0) {
			self.selected = selected
			self.limit = limit
		}
	}

	public enum Action: Equatable {
		case subscribeToAlleys
		case dismissButtonTapped
		case errorButtonTapped
		case saveButtonTapped
		case alleyTapped(Alley)
		case alleysResponse(TaskResult<[Alley]>)
	}

	public init() {}

	@Dependency(\.alleysDataProvider) var alleysDataProvider

	public var body: some ReducerProtocol<State, Action> {
		Reduce { state, action in
			switch action {
			case .subscribeToAlleys:
				state.error = nil
				return .run { send in
					for try await alleys in alleysDataProvider.fetchAlleys(.init(ordering: .byRecentlyUsed)) {
						await send(.alleysResponse(.success(alleys)))
					}
				} catch: { error, send in
					await send(.alleysResponse(.failure(error)))
				}

			case let .alleyTapped(alley):
				if state.selected.contains(alley.id) {
					state.selected.remove(alley.id)
				} else if state.selected.count < state.limit || state.limit == 0 {
					state.selected.insert(alley.id)
				}
				return .none

			case let .alleysResponse(.success(alleys)):
				state.alleys = .init(uniqueElements: alleys)
				return .none

			case .alleysResponse(.failure):
				state.error = .loadError
				return .none

			case .errorButtonTapped, .dismissButtonTapped, .saveButtonTapped:
				return .none
			}
		}
	}
}

extension ListErrorContent {
	static let loadError = Self(
		title: "Something went wrong!",
		message: "We couldn't load your data",
		action: "Try again"
	)
}
