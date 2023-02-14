import BaseFormLibrary
import BowlersDataProviderInterface
import ComposableArchitecture
import FeatureActionLibrary
import PersistenceServiceInterface
import ResourcePickerLibrary
import SharedModelsLibrary
import StringsLibrary

extension Team: BaseFormModel {
	static public var modelName: String = Strings.Team.title
}

extension Bowler: PickableResource {
	static public func pickableModelName(forCount count: Int) -> String {
		count == 1 ? Strings.Bowler.title : Strings.Bowler.List.title
	}
}

public struct TeamEditor: ReducerProtocol {
	public typealias Form = BaseForm<Team, Fields>

	public struct Fields: BaseFormState, Equatable {
		public let teamId: Team.ID
		public var bowlers: ResourcePicker<Bowler, Bowler.FetchRequest>.State
		@BindableState public var name = ""

		public let isDeleteable = true
		public var isSaveable: Bool {
			!name.isEmpty && !bowlers.selected.isEmpty
		}

		public init(teamId: Team.ID, bowlers: [Bowler]) {
			self.teamId = teamId
			self.bowlers = .init(
				selected: Set(bowlers.map(\.id)),
				query: .init(filter: nil, ordering: .byName)
			)
		}
	}

	public struct State: Equatable {
		public var base: Form.State
		public var teamMembers: TeamMembers.State
		public var isBowlerPickerPresented = false

		public init(mode: Form.Mode, bowlers: [Bowler]) {
			var fields: Fields
			switch mode {
			case let .edit(team):
				fields = .init(teamId: team.id, bowlers: bowlers)
				fields.name = team.name
				teamMembers = .init(team: team)
			case .create:
				@Dependency(\.uuid) var uuid: UUIDGenerator

				fields = .init(teamId: uuid(), bowlers: [])
				teamMembers = .init(team: nil)
			}

			self.base = .init(mode: mode, form: fields)
		}
	}

	public enum Action: FeatureAction, BindableAction, Equatable {
		public enum ViewAction: Equatable {
			case setBowlerPicker(isPresented: Bool)
		}
		public enum DelegateAction: Equatable {
			case didFinishEditing
		}
		public enum InternalAction: Equatable {
			case bowlers(ResourcePicker<Bowler, Bowler.FetchRequest>.Action)
			case teamMembers(TeamMembers.Action)
			case form(Form.Action)
		}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
		case binding(BindingAction<State>)
	}

	public init() {}

	@Dependency(\.persistenceService) var persistenceService
	@Dependency(\.bowlersDataProvider) var bowlersDataProvider

	public var body: some ReducerProtocol<State, Action> {
		BindingReducer()

		Scope(state: \.base, action: /Action.internal..Action.InternalAction.form) {
			BaseForm()
				.dependency(\.modelPersistence, .init(
					create: persistenceService.createTeam,
					update: persistenceService.updateTeam,
					delete: persistenceService.deleteTeam
				))
		}

		Scope(state: \.teamMembers, action: /Action.internal..Action.InternalAction.teamMembers) {
			TeamMembers()
		}

		Scope(state: \.base.form.bowlers, action: /Action.internal..Action.InternalAction.bowlers) {
			ResourcePicker {
				try await bowlersDataProvider.fetchBowlers($0)
			}
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .internal(internalAction):
				switch internalAction {
				case let .form(.delegate(delegateAction)):
					switch delegateAction {
					case let .didSaveModel(team):
						let members = state.base.form.bowlers.selectedResources ?? state.teamMembers.bowlers?.elements
						let teamMembership = TeamMembership(team: team.id, members: members ?? [])
						return .task { [teamMembership = teamMembership] in
							try await persistenceService.updateTeamMembers(teamMembership)

							return .internal(.form(.callback(.didFinishSaving(.success(team)))))
						} catch: { error in
							return .internal(.form(.callback(.didFinishSaving(.failure(error)))))
						}

					case let .didDeleteModel(team):
						return .task { .internal(.form(.callback(.didFinishDeleting(.success(team))))) }

					case .didFinishSaving, .didFinishDeleting:
						return .task { .delegate(.didFinishEditing) }
					}

				case let .bowlers(.delegate(delegateAction)):
					switch delegateAction {
					case .didFinishEditing:
						state.teamMembers.bowlers = .init(
							uniqueElements: state.base.form.bowlers.selectedResources?.sorted { $0.name < $1.name } ?? []
						)
						state.isBowlerPickerPresented = false
						return .none
					}

				case let .teamMembers(.delegate(delegateAction)):
					switch delegateAction {
					case .never:
						return .none
					}

				case .teamMembers(.view), .teamMembers(.internal):
					return .none

				case .form(.view), .form(.internal), .form(.callback):
					return .none

				case .bowlers(.view), .bowlers(.internal):
					return .none
				}

			case let .view(viewAction):
				switch viewAction {
				case let .setBowlerPicker(isPresented):
					state.isBowlerPickerPresented = isPresented
					return .none
				}

			case .delegate, .binding:
				return .none
			}
		}
	}
}

extension TeamEditor.Fields {
	public func model(fromExisting existing: Team?) -> Team {
		return .init(
			id: teamId,
			name: name
		)
	}
}
