import ComposableArchitecture
import ModelsLibrary

extension AlleyEditor.State {
	var alleyId: Alley.ID {
		switch initialValue {
		case let .create(create): return create.id
		case let .edit(edit): return edit.id
		}
	}

	var form: AlleyForm.State {
		get {
			var form = _form
			switch initialValue {
			case var .create(new):
				new.name = name
				new.material = material
				new.pinFall = pinFall
				new.mechanism = mechanism
				new.pinBase = pinBase
				new.location = location
				form.value = .create(new)
			case var .edit(existing):
				existing.name = name
				existing.material = material
				existing.pinFall = pinFall
				existing.mechanism = mechanism
				existing.pinBase = pinBase
				existing.location = location
				form.value = .edit(existing)
			}
			return form
		}
		set {
			_form = newValue
		}
	}
}
