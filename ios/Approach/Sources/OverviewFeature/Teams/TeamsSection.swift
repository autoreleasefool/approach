import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import ModelsLibrary
import RecentlyUsedServiceInterface
import ResourceListLibrary
import StringsLibrary
import SwiftUI
import TeamsRepositoryInterface

extension Team.List: ResourceListSectionItem {}

@Reducer
public struct TeamsSection: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		@Shared(.teamsFetchRequest) public var teamsFetchRequest
		public var teams: ResourceListSection<Team.List, Team.List.FetchRequest>.State

		init() {
			let teamsFetchRequest = Shared(.teamsFetchRequest)
			self._teamsFetchRequest = teamsFetchRequest

			self.teams = .init(
				features: [.swipeToArchive, .swipeToEdit],
				query: SharedReader(teamsFetchRequest),
				emptyContent: .init(
					image: Asset.Media.EmptyState.teams,
					title: Strings.Team.Error.Empty.title,
					message: Strings.Team.Error.Empty.message,
					action: Strings.Team.List.add
				)
			)
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case didTapTeam(Team.ID)
		}

		@CasePathable
		public enum Internal {
			case didArchiveTeam(Result<Team.List, Error>)
			case teams(ResourceListSection<Team.List, Team.List.FetchRequest>.Action)
		}

		@CasePathable
		public enum Delegate {
			case didReceiveError(ErrorID, Error, message: String)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	public enum ErrorID {
		case teamNotFound
		case failedToArchive
	}

	@Dependency(TeamsRepository.self) var teams
	@Dependency(RecentlyUsedService.self) var recentlyUsed
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Scope(state: \.teams, action: \.internal.teams) {
			ResourceListSection(fetchResources: teams.list)
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didTapTeam(id):
					guard let team = state.teams.findResource(byId: id) else { return .none }
					return recentlyUsed.didRecentlyUse(.teams, id: team.id, in: self)
				}

			case let .internal(internalAction):
				switch internalAction {
				case .didArchiveTeam(.success):
					return .none

				case let .didArchiveTeam(.failure(error)):
					return .send(.delegate(.didReceiveError(
						.failedToArchive,
						error,
						message: Strings.Error.Toast.failedToArchive
					)))

				case let .teams(.delegate(delegateAction)):
					switch delegateAction {
					case .didEdit:
						// TODO: edit team
						return .none

					case let .didArchive(team):
						return .run { send in
							await send(.internal(.didArchiveTeam(Result {
								// TODO: archive team
								team
							})))
						}

					case .didTapEmptyStateButton:
						// TODO: create team
						return .none

					case .didDelete:
						return .none
					}

				case .teams(.internal), .teams(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

@ViewAction(for: TeamsSection.self)
public struct TeamsSectionView: View {
	@Bindable public var store: StoreOf<TeamsSection>

	init(store: StoreOf<TeamsSection>) {
		self.store = store
	}

	public var body: some View {
		Section(Strings.Overview.List.teams) {
			ResourceListSectionView(
				store: store.scope(state: \.teams, action: \.internal.teams)
			) { team in
				VStack(spacing: .unitSpacing) {
					Text(team.name)
					Text(team.bowlers.map(\.name).joined(separator: ", "))
				}
			}
		}
	}
}
