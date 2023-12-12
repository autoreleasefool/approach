import ComposableArchitecture

extension GearEditor.State {
	var form: GearForm.State {
		get {
			var form = _form
			switch initialValue {
			case var .create(new):
				new.name = name
				new.kind = kind
				new.owner = owner
				new.avatar = avatar
				form.value = .create(new)
			case var .edit(existing):
				existing.name = name
				existing.owner = owner
				existing.avatar = avatar
				form.value = .edit(existing)
			}
			return form
		}
		set {
			_form = newValue
		}
	}
}
