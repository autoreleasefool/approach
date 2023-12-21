import AlleysRepositoryInterface
import AnalyticsServiceInterface
import ComposableArchitecture
import EquatableLibrary
import FeatureActionLibrary
import FormFeature
import LeaguesRepositoryInterface
import ModelsLibrary
import PickableModelsLibrary
import ResourcePickerLibrary
import StringsLibrary

public typealias LeagueForm = Form<League.Create, League.Edit>

@Reducer
public struct LeagueEditor: Reducer {
	public struct State: Equatable {
		@BindingState public var name: String
		@BindingState public var recurrence: League.Recurrence
		@BindingState public var defaultNumberOfGames: Int
		@BindingState public var additionalPinfall: String
		@BindingState public var additionalGames: String
		@BindingState public var excludeFromStatistics: League.ExcludeFromStatistics
		@BindingState public var coordinate: CoordinateRegion
		public var location: Alley.Summary?

		@BindingState public var gamesPerSeries: GamesPerSeries
		@BindingState public var hasAdditionalPinfall: Bool
		public var shouldShowLocationSection: Bool

		public let initialValue: LeagueForm.Value
		public var _form: LeagueForm.State

		@PresentationState public var alleyPicker: ResourcePicker<Alley.Summary, AlwaysEqual<Void>>.State?

		public init(value: LeagueForm.Value) {
			let defaultNumberOfGames: Int
			let additionalGames: Int
			switch value {
			case let .create(new):
				self.name = new.name
				self.recurrence = new.recurrence
				let additionalPinfall = new.additionalPinfall ?? 0
				self.additionalPinfall = additionalPinfall > 0 ? String(additionalPinfall) : ""
				self.excludeFromStatistics = new.excludeFromStatistics
				self.location = new.location
				self.coordinate = .init(coordinate: .init())
				defaultNumberOfGames = new.defaultNumberOfGames ?? 0
				additionalGames = new.additionalGames ?? 0
				self.gamesPerSeries = .dynamic
				self.shouldShowLocationSection = new.recurrence.shouldShowLocationSection
				self.initialValue = .create(new)
			case let .edit(existing):
				self.name = existing.name
				self.recurrence = existing.recurrence
				let additionalPinfall = existing.additionalPinfall ?? 0
				self.additionalPinfall = additionalPinfall > 0 ? String(additionalPinfall) : ""
				self.excludeFromStatistics = existing.excludeFromStatistics
				self.location = existing.location
				self.coordinate = .init(coordinate: existing.location?.location?.coordinate.mapCoordinate ?? .init())
				defaultNumberOfGames = existing.defaultNumberOfGames ?? 0
				additionalGames = existing.additionalGames ?? 0
				self.shouldShowLocationSection = existing.recurrence.shouldShowLocationSection
				self.gamesPerSeries = defaultNumberOfGames == 0 ? .dynamic : .static
				self.initialValue = .edit(existing)
			}
			self._form = .init(initialValue: self.initialValue, currentValue: self.initialValue)
			self.hasAdditionalPinfall = additionalGames > 0
			self.defaultNumberOfGames = max(defaultNumberOfGames, 1)
			self.additionalGames = additionalGames > 0 ? String(additionalGames) : ""
		}
	}

	public enum GamesPerSeries: Int, Equatable, Identifiable, CaseIterable, CustomStringConvertible {
		case `static`
		case dynamic

		public var description: String {
			switch self {
			case .static: return Strings.League.Editor.Fields.GamesPerSeries.constant
			case .dynamic: return Strings.League.Editor.Fields.GamesPerSeries.alwaysAskMe
			}
		}

		public var id: Int { rawValue }
	}

	public enum Action: FeatureAction {
		@CasePathable public enum ViewAction: BindableAction {
			case onAppear
			case didTapAlley
			case binding(BindingAction<State>)
		}
		@CasePathable public enum DelegateAction {
			case didFinishCreating(League.Create)
			case didFinishUpdating(League.Edit)
			case didFinishArchiving(League.Edit)
		}
		@CasePathable public enum InternalAction {
			case setLocationSection(isShown: Bool)
			case form(LeagueForm.Action)
			case alleyPicker(PresentationAction<ResourcePicker<Alley.Summary, AlwaysEqual<Void>>.Action>)
		}

		case view(ViewAction)
		case `internal`(InternalAction)
		case delegate(DelegateAction)
	}

	public init() {}

	@Dependency(\.alleys) var alleys
	@Dependency(\.dismiss) var dismiss
	@Dependency(\.leagues) var leagues
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		BindingReducer(action: \.view)

		Scope(state: \.form, action: \.internal.form) {
			LeagueForm()
				.dependency(\.records, .init(
					create: leagues.create,
					update: leagues.update,
					delete: { _ in },
					archive: leagues.archive
				))
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didTapAlley:
					state.alleyPicker = .init(
						selected: Set([state.location?.id].compactMap { $0 }),
						query: .init(()),
						limit: 1,
						showsCancelHeaderButton: false
					)
					return .none

				case .binding(\.$recurrence):
					switch state.recurrence {
					case .once:
						state.gamesPerSeries = .static
						return .run { send in await send(.internal(.setLocationSection(isShown: true)), animation: .easeInOut) }
					case .repeating:
						state.location = nil
						return .run { send in await send(.internal(.setLocationSection(isShown: false)), animation: .easeInOut) }
					}

				case .binding:
					return .none
				}

			case let .internal(internalAction):
				switch internalAction {
				case let .setLocationSection(isShown):
					state.shouldShowLocationSection = isShown
					return .none

				case let .alleyPicker(.presented(.delegate(delegateAction))):
					switch delegateAction {
					case let .didChangeSelection(alley):
						state.location = alley.first
						state.coordinate = .init(coordinate: state.location?.location?.coordinate.mapCoordinate ?? .init())
						return .none
					}

				case let .form(.delegate(delegateAction)):
					switch delegateAction {
					case let .didCreate(result):
						return state._form.didFinishCreating(result)
							.map { .internal(.form($0)) }

					case let .didUpdate(result):
						return state._form.didFinishUpdating(result)
							.map { .internal(.form($0)) }

					case let .didArchive(result):
						return state._form.didFinishDeleting(result)
							.map { .internal(.form($0)) }

					case let  .didFinishArchiving(league):
						return .concatenate(
							.send(.delegate(.didFinishArchiving(league))),
							.run { _ in await dismiss() }
						)

					case let  .didFinishUpdating(league):
						return .concatenate(
							.send(.delegate(.didFinishUpdating(league))),
							.run { _ in await dismiss() }
						)

					case let  .didFinishCreating(league):
						return .concatenate(
							.send(.delegate(.didFinishCreating(league))),
							.run { _ in await dismiss() }
						)

					case .didDiscard, .didDelete, .didFinishDeleting:
						return .run { _ in await dismiss() }
					}

				case .form(.internal), .form(.view):
					return .none

				case .alleyPicker(.presented(.internal)), .alleyPicker(.presented(.view)), .alleyPicker(.dismiss):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$alleyPicker, action: \.internal.alleyPicker) {
			ResourcePicker { _ in alleys.pickable() }
		}

		AnalyticsReducer<State, Action> { _, action in
			switch action {
			case .internal(.form(.delegate(.didFinishCreating))):
				return Analytics.League.Created()
			case .internal(.form(.delegate(.didFinishUpdating))):
				return Analytics.League.Updated()
			case .internal(.form(.delegate(.didFinishArchiving))):
				return Analytics.League.Archived()
			default:
				return nil
			}
		}

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}
	}
}

extension League.Create: CreateableRecord {
	public static var modelName = Strings.League.title

	public var isSaveable: Bool {
		!name.isEmpty
	}
}

extension League.Edit: EditableRecord {
	public var isDeleteable: Bool { false }
	public var isArchivable: Bool { true }
	public var isSaveable: Bool {
		!name.isEmpty
	}
}

extension League.Recurrence: CustomStringConvertible {
	public var description: String {
		switch self {
		case .repeating: return Strings.League.Properties.Recurrence.repeats
		case .once: return Strings.League.Properties.Recurrence.never
		}
	}

	var shouldShowLocationSection: Bool {
		switch self {
		case .repeating:
			return false
		case .once:
			return true
		}
	}
}

extension League.ExcludeFromStatistics: CustomStringConvertible {
	public var description: String {
		switch self {
		case .include: return Strings.League.Properties.ExcludeFromStatistics.include
		case .exclude: return Strings.League.Properties.ExcludeFromStatistics.exclude
		}
	}
}
